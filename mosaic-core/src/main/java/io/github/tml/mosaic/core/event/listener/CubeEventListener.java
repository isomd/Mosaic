package io.github.tml.mosaic.core.event.listener;

import io.github.tml.mosaic.core.event.event.CubeEvent;

/**
 * 描述: 事件监听器基础接口
 * @author suifeng
 * 日期: 2025/6/13
 */
public interface CubeEventListener<T extends CubeEvent> {

    /**
     * 处理事件
     * @param event 事件对象
     */
    void onEvent(T event);

    /**
     * 获取监听的事件类型（核心方法）
     * 子类必须明确返回监听的事件类型
     */
    Class<T> getEventType();

    /**
     * 获取监听器优先级，数字越小优先级越高
     */
    default int getPriority() {
        return Integer.MAX_VALUE;
    }

    /**
     * 是否支持异步处理
     */
    default boolean isAsyncSupported() {
        return false;
    }

    /**
     * 获取监听器名称
     */
    default String getListenerName() {
        return this.getClass().getSimpleName();
    }
}