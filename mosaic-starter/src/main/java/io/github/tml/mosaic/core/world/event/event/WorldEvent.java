package io.github.tml.mosaic.core.world.event.event;

import io.github.tml.mosaic.core.event.event.MosaicEvent;
import io.github.tml.mosaic.core.tools.guid.GUID;
import lombok.Data;

@Data
public abstract class WorldEvent extends MosaicEvent {
    private GUID oldWorldId;

    private GUID newWorldId;

    protected WorldEvent(Object source) {
        super(source);
    }
}
