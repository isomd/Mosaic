package io.github.tml.mosaic.core.world.event.event;

import io.github.tml.mosaic.core.event.event.MosaicEvent;

public abstract class WorldEvent extends MosaicEvent {
    protected WorldEvent(Object source) {
        super(source);
    }
}
