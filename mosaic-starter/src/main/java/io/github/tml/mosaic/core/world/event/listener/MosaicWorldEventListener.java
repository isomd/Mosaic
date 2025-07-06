package io.github.tml.mosaic.core.world.event.listener;

import io.github.tml.mosaic.core.event.listener.MosaicEventListener;
import io.github.tml.mosaic.core.world.event.WorldEventBroadcaster;
import io.github.tml.mosaic.core.world.event.event.WorldEvent;
import org.springframework.stereotype.Component;

@Component
public class MosaicWorldEventListener implements MosaicEventListener<WorldEvent> {
    private final WorldEventBroadcaster worldEventBroadcaster;

    public MosaicWorldEventListener(WorldEventBroadcaster worldEventBroadcaster) {
        this.worldEventBroadcaster = worldEventBroadcaster;
    }

    @Override
    public void onEvent(WorldEvent event) {
        this.worldEventBroadcaster.broadcastEvent(event);
    }

    @Override
    public Class<WorldEvent> getEventType() {
        return WorldEvent.class;
    }

    @Override
    public boolean isAsyncSupported() {
        return true;
    }
}
