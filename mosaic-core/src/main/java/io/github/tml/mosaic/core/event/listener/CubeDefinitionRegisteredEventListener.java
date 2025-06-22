package io.github.tml.mosaic.core.event.listener;

import io.github.tml.mosaic.core.event.event.CubeDefinitionRegisteredEvent;

/**
 * 描述: CubeDefinition注册事件监听器
 * @author suifeng
 * 日期: 2025/6/13
 */
public interface CubeDefinitionRegisteredEventListener extends MosaicEventListener<CubeDefinitionRegisteredEvent> {

    @Override
    default Class<CubeDefinitionRegisteredEvent> getEventType() {
        return CubeDefinitionRegisteredEvent.class;
    }
}