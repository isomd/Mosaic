package io.github.tml.mosaic.core.event;

import io.github.tml.mosaic.core.event.event.MosaicEvent;
import io.github.tml.mosaic.core.event.listener.MosaicEventListener;
import java.util.Set;

/**
 * 描述: 事件广播器接口
 * @author suifeng
 * 日期: 2025/6/13
 */
public interface MosaicEventBroadcaster {
    
    /**
     * 注册事件监听器
     */
    <T extends MosaicEvent> void registerListener(MosaicEventListener<T> listener);
    
    /**
     * 注销事件监听器
     */
    <T extends MosaicEvent> boolean unregisterListener(MosaicEventListener<T> listener);
    
    /**
     * 广播事件
     */
    void broadcastEvent(MosaicEvent event);
    
    /**
     * 获取监听器总数
     */
    int getListenerCount();
    
    /**
     * 获取指定事件类型的监听器数量
     */
    int getListenerCount(Class<? extends MosaicEvent> eventType);
    
    /**
     * 获取支持的事件类型
     */
    Set<Class<? extends MosaicEvent>> getSupportedEventTypes();
    
    /**
     * 关闭广播器
     */
    void shutdown();
}