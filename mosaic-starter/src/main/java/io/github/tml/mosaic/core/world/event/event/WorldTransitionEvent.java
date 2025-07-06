package io.github.tml.mosaic.core.world.event.event;

import lombok.Data;

@Data
public class WorldTransitionEvent extends WorldEvent {

    protected WorldTransitionEvent(Object source) {
        super(source);
    }

    @Override
    public String getEventType() {
        return "WORLD_TRAVERSE_EVENT";
    }
}
