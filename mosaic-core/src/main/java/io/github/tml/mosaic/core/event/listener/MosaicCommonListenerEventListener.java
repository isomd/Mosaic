package io.github.tml.mosaic.core.event.listener;

import io.github.tml.mosaic.core.event.event.MosaicEvent;

/**
 * 描述: 全部事件监听器
 * @author suifeng
 * 日期: 2025/6/13
 */
public interface MosaicCommonListenerEventListener extends MosaicEventListener<MosaicEvent> {

    @Override
    default Class<MosaicEvent> getEventType() {
        return MosaicEvent.class;
    }
}