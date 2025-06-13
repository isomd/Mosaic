package io.github.tml.mosaic.core.event;

import io.github.tml.mosaic.core.event.event.CubeEvent;
import io.github.tml.mosaic.core.event.listener.CubeEventListener;
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
public class DefaultCubeEventBroadcaster implements CubeEventBroadcaster {

    private final Map<Class<? extends CubeEvent>, List<CubeEventListener<?>>> listenerRegistry = new ConcurrentHashMap<>();
    private final ExecutorService asyncExecutor;
    private final Object registryLock = new Object();
    private volatile boolean shutdown = false;
    private final static DefaultCubeEventBroadcaster BROADCASTER = new DefaultCubeEventBroadcaster();

    public static DefaultCubeEventBroadcaster broadcaster() {
        return BROADCASTER;
    }

    public DefaultCubeEventBroadcaster() {
        this.asyncExecutor = createAsyncExecutor();
    }

    @Override
    public <T extends CubeEvent> void registerListener(CubeEventListener<T> listener) {
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
            listenerRegistry.get(eventType).sort(Comparator.comparingInt(CubeEventListener::getPriority));
        }

        log.debug("Registered event listener [{}] for event type [{}] with priority [{}]",
                listener.getListenerName(), eventType.getSimpleName(), listener.getPriority());
    }

    @Override
    public <T extends CubeEvent> boolean unregisterListener(CubeEventListener<T> listener) {
        if (listener == null) return false;

        Class<T> eventType = listener.getEventType();
        if (eventType == null) return false;

        synchronized (registryLock) {
            List<CubeEventListener<?>> listeners = listenerRegistry.get(eventType);
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
    public void broadcastEvent(CubeEvent event) {
        if (event == null || shutdown) return;

        Class<? extends CubeEvent> eventType = event.getClass();
        List<CubeEventListener<?>> listeners = getListenersForEvent(eventType);

        if (listeners.isEmpty()) {
            log.debug("No listeners found for event type [{}]", eventType.getSimpleName());
            return;
        }

        log.debug("Broadcasting event [{}] to [{}] listeners", event.getEventType(), listeners.size());

        List<CompletableFuture<Void>> asyncTasks = new ArrayList<>();

        for (CubeEventListener<?> listener : listeners) {
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
    public int getListenerCount(Class<? extends CubeEvent> eventType) {
        List<CubeEventListener<?>> listeners = listenerRegistry.get(eventType);
        return listeners != null ? listeners.size() : 0;
    }

    @Override
    public Set<Class<? extends CubeEvent>> getSupportedEventTypes() {
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

    private List<CubeEventListener<?>> getListenersForEvent(Class<? extends CubeEvent> eventType) {
        List<CubeEventListener<?>> result = new ArrayList<>();

        // 获取精确匹配的监听器
        List<CubeEventListener<?>> exactMatches = listenerRegistry.get(eventType);
        if (exactMatches != null) {
            result.addAll(exactMatches);
        }

        // 获取父类型的监听器（支持事件继承）
        for (Map.Entry<Class<? extends CubeEvent>, List<CubeEventListener<?>>> entry : listenerRegistry.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventType) && !entry.getKey().equals(eventType)) {
                result.addAll(entry.getValue());
            }
        }

        return result.stream()
                .sorted(Comparator.comparingInt(CubeEventListener::getPriority))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private void invokeListener(CubeEventListener<?> listener, CubeEvent event) {
        try {
            // 类型安全检查
            if (listener.getEventType().isAssignableFrom(event.getClass())) {
                ((CubeEventListener<CubeEvent>) listener).onEvent(event);
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

    private void handleListenerException(CubeEventListener<?> listener, CubeEvent event, Throwable throwable) {
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
                        Thread thread = new Thread(r, "CubeEventBroadcaster-" + (++counter));
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
    }
}