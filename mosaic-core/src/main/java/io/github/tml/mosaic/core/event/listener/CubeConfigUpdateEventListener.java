package io.github.tml.mosaic.core.event.listener;

import io.github.tml.mosaic.core.event.event.CubeConfigUpdateEvent;

/**
 * 描述: CubeConfig更新事件监听器
 * @author suifeng
 * 日期: 2025/6/13
 */
public interface CubeConfigUpdateEventListener extends MosaicEventListener<CubeConfigUpdateEvent> {

    @Override
    default Class<CubeConfigUpdateEvent> getEventType() {
        return CubeConfigUpdateEvent.class;
    }
}