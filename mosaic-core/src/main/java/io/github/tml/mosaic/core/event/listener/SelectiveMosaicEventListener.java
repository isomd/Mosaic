package io.github.tml.mosaic.core.event.listener;

import io.github.tml.mosaic.core.event.event.MosaicEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

/**
 * 描述: 选择性事件监听器抽象基类
 * 提供链式事件处理机制
 * @author suifeng
 * 日期: 2025/7/8
 */
@Slf4j
public abstract class SelectiveMosaicEventListener implements MosaicCommonListenerEventListener {

    /**
     * 获取监听的事件类型列表
     */
    protected abstract List<Class<? extends MosaicEvent>> getListenedEventTypes();

    @Override
    public final void onEvent(MosaicEvent event) {
        if (event == null || !isListenedEventType(event.getClass())) {
            return;
        }

        try {
            onSelectiveEvent(event);
        } catch (Exception e) {
            log.error("Error processing event [{}] in listener [{}]: {}",
                    event.getEventType(), getListenerName(), e.getMessage(), e);
        }
    }

    /**
     * 处理筛选后的事件
     */
    protected abstract void onSelectiveEvent(MosaicEvent event);

    /**
     * 处理未匹配的事件类型
     * 子类可重写此方法来自定义未处理事件的逻辑
     */
    protected void onUnhandledEvent(MosaicEvent event) {
        log.warn("Unhandled event type: [{}] in listener [{}]",
                event.getClass().getSimpleName(), getListenerName());
    }

    /**
     * 检查是否是监听的事件类型
     */
    private boolean isListenedEventType(Class<? extends MosaicEvent> eventType) {
        List<Class<? extends MosaicEvent>> listenedTypes = getListenedEventTypes();
        return listenedTypes != null && listenedTypes.stream()
                .anyMatch(type -> type.isAssignableFrom(eventType));
    }

    // ================ 核心工具方法 ================

    /**
     * 类型匹配处理器 - 核心方法
     * 如果事件类型匹配，则执行处理逻辑
     */
    protected <T extends MosaicEvent> EventProcessor when(MosaicEvent event, Class<T> type, Consumer<T> handler) {
        if (type.isInstance(event)) {
            handler.accept(type.cast(event));
            return new EventProcessor(true, event);
        }
        return new EventProcessor(false, event);
    }

    /**
     * 事件处理器包装类，支持链式调用
     */
    protected class EventProcessor {
        private final boolean handled;
        private final MosaicEvent event;

        EventProcessor(boolean handled, MosaicEvent event) {
            this.handled = handled;
            this.event = event;
        }

        /**
         * 链式处理下一个事件类型
         */
        public <T extends MosaicEvent> EventProcessor orWhen(Class<T> type, Consumer<T> handler) {
            if (handled) {
                return this;
            }
            return SelectiveMosaicEventListener.this.when(event, type, handler);
        }

        /**
         * 兜底处理 - 当没有匹配的类型时执行自定义逻辑
         */
        public void orElse(Runnable handler) {
            if (!handled) {
                handler.run();
            }
        }

        /**
         * 兜底处理 - 调用抽象类的未处理事件方法
         */
        public void orDefault() {
            if (!handled) {
                SelectiveMosaicEventListener.this.onUnhandledEvent(event);
            }
        }
    }

    // ================ 辅助工具方法 ================

    /**
     * 获取事件类型名称
     */
    protected String getTypeName(MosaicEvent event) {
        return event.getClass().getSimpleName();
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean isAsyncSupported() {
        return true;
    }
}