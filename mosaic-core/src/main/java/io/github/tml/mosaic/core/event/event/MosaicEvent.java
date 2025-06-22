package io.github.tml.mosaic.core.event.event;

import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import io.github.tml.mosaic.core.tools.guid.GUID;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 描述: 事件基类，定义所有事件的通用属性
 * @author suifeng
 * 日期: 2025/6/13
 */
@Data
public abstract class MosaicEvent {

    private final GUID eventId;
    private final LocalDateTime timestamp;
    private final Object source;
    
    protected MosaicEvent(Object source) {
        this.eventId = CommonComponent.GuidAllocator().nextGUID();
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }

    public abstract String getEventType();
}