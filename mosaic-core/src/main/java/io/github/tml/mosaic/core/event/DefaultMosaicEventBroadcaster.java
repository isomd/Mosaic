package io.github.tml.mosaic.core.event;

import io.github.tml.mosaic.core.event.event.MosaicEvent;
import io.github.tml.mosaic.core.event.listener.MosaicCommonListenerEventListener;
import io.github.tml.mosaic.core.event.listener.MosaicEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 描述: 事件广播器实现类
 * @author suifeng
 * 日期: 2025/6/14
 */
@Slf4j
public class DefaultMosaicEventBroadcaster implements MosaicEventBroadcaster {

    private final Map<Class<? extends MosaicEvent>, List<MosaicEventListener<?>>> listenerRegistry = new ConcurrentHashMap<>();
    private final ExecutorService asyncExecutor;
    private final Object registryLock = new Object();
    private volatile boolean shutdown = false;
    private final static DefaultMosaicEventBroadcaster BROADCASTER = new DefaultMosaicEventBroadcaster();

    public static DefaultMosaicEventBroadcaster broadcaster() {
        return BROADCASTER;
    }

    public DefaultMosaicEventBroadcaster() {
        this.asyncExecutor = createAsyncExecutor();
    }

    @Override
    public <T extends MosaicEvent> void registerListener(MosaicEventListener<T> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Event listener cannot be null");
        }

        // 直接调用监听器的getEventType方法，简单可靠
        Class<T> eventType = listener.getEventType();
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null for listener: " + listener.getClass());
        }

        synchronized (registryLock) {
            listenerRegistry.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
            // 按优先级排序
            listenerRegistry.get(eventType).sort(Comparator.comparingInt(MosaicEventListener::getPriority));
        }

        log.debug("Registered event listener [{}] for event type [{}] with priority [{}]",
                listener.getListenerName(), eventType.getSimpleName(), listener.getPriority());
    }

    @Override
    public <T extends MosaicEvent> boolean unregisterListener(MosaicEventListener<T> listener) {
        if (listener == null) return false;

        Class<T> eventType = listener.getEventType();
        if (eventType == null) return false;

        synchronized (registryLock) {
            List<MosaicEventListener<?>> listeners = listenerRegistry.get(eventType);
            if (listeners != null) {
                boolean removed = listeners.remove(listener);
                if (listeners.isEmpty()) {
                    listenerRegistry.remove(eventType);
                }
                if (removed) {
                    log.debug("Unregistered event listener [{}] for event type [{}]",
                            listener.getListenerName(), eventType.getSimpleName());
                }
                return removed;
            }
        }
        return false;
    }

    @Override
    public void broadcastEvent(MosaicEvent event) {
        if (event == null || shutdown) return;

        Class<? extends MosaicEvent> eventType = event.getClass();
        List<MosaicEventListener<?>> listeners = getListenersForEvent(eventType);

        if (listeners.isEmpty()) {
            log.debug("No listeners found for event type [{}]", eventType.getSimpleName());
            return;
        }

        log.debug("Broadcasting event [{}] to [{}] listeners", event.getEventType(), listeners.size());

        List<CompletableFuture<Void>> asyncTasks = new ArrayList<>();

        for (MosaicEventListener<?> listener : listeners) {
            try {
                if (listener.isAsyncSupported() && !shutdown) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(
                                    () -> invokeListener(listener, event), asyncExecutor)
                            .exceptionally(throwable -> {
                                handleListenerException(listener, event, throwable);
                                return null;
                            });
                    asyncTasks.add(future);
                } else {
                    invokeListener(listener, event);
                }
            } catch (Exception e) {
                handleListenerException(listener, event, e);
            }
        }

        // 等待异步任务完成
        if (!asyncTasks.isEmpty()) {
            CompletableFuture.allOf(asyncTasks.toArray(new CompletableFuture[0]))
                    .orTimeout(30, TimeUnit.SECONDS)
                    .exceptionally(throwable -> {
                        log.warn("Some async event listeners did not complete within timeout", throwable);
                        return null;
                    });
        }
    }

    @Override
    public int getListenerCount() {
        return listenerRegistry.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    @Override
    public int getListenerCount(Class<? extends MosaicEvent> eventType) {
        List<MosaicEventListener<?>> listeners = listenerRegistry.get(eventType);
        return listeners != null ? listeners.size() : 0;
    }

    @Override
    public Set<Class<? extends MosaicEvent>> getSupportedEventTypes() {
        return new HashSet<>(listenerRegistry.keySet());
    }

    @Override
    public void shutdown() {
        shutdown = true;
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Event broadcaster has been shutdown");
    }

    private List<MosaicEventListener<?>> getListenersForEvent(Class<? extends MosaicEvent> eventType) {
        List<MosaicEventListener<?>> result = new ArrayList<>();

        // 1. 获取精确匹配的监听器
        List<MosaicEventListener<?>> exactMatches = listenerRegistry.get(eventType);
        if (exactMatches != null) {
            result.addAll(exactMatches);
        }

        // 2. 获取父类型的监听器（支持事件继承）
        for (Map.Entry<Class<? extends MosaicEvent>, List<MosaicEventListener<?>>> entry : listenerRegistry.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventType) && !entry.getKey().equals(eventType)) {
                result.addAll(entry.getValue());
            }
        }

        // 3. 【新增】强制添加所有MosaicCommonListenerEventListener
        List<MosaicEventListener<?>> commonListeners = listenerRegistry.get(MosaicEvent.class);
        if (commonListeners != null) {
            for (MosaicEventListener<?> listener : commonListeners) {
                if (listener instanceof MosaicCommonListenerEventListener && !result.contains(listener)) {
                    result.add(listener);
                }
            }
        }

        return result.stream()
                .sorted(Comparator.comparingInt(MosaicEventListener::getPriority))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private void invokeListener(MosaicEventListener<?> listener, MosaicEvent event) {
        try {
            // 类型安全检查
            if (listener.getEventType().isAssignableFrom(event.getClass())) {
                ((MosaicEventListener<MosaicEvent>) listener).onEvent(event);
                log.trace("Successfully invoked listener [{}] for event [{}]",
                        listener.getListenerName(), event.getEventType());
            } else {
                log.warn("Event type mismatch for listener [{}]. Expected: [{}], Actual: [{}]",
                        listener.getListenerName(), listener.getEventType().getSimpleName(),
                        event.getClass().getSimpleName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error invoking listener: " + listener.getListenerName(), e);
        }
    }

    private void handleListenerException(MosaicEventListener<?> listener, MosaicEvent event, Throwable throwable) {
        log.error("Error occurred while processing event [{}] in listener [{}]: {}",
                event.getEventType(), listener.getListenerName(), throwable.getMessage(), throwable);
    }

    private ExecutorService createAsyncExecutor() {
        return new ThreadPoolExecutor(
                2, // 核心线程数
                10, // 最大线程数
                60L, TimeUnit.SECONDS, // 空闲线程存活时间
                new LinkedBlockingQueue<>(1000), // 工作队列
                new ThreadFactory() {
                    private int counter = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "MosaicEventBroadcaster-" + (++counter));
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
    }
}