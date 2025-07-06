package io.github.tml.mosaic.core.world.event.listener;

import io.github.tml.mosaic.core.event.listener.MosaicEventListener;
import io.github.tml.mosaic.core.world.event.event.WorldTransitionEvent;
import org.springframework.stereotype.Component;

// TODO: 处理世界切换事件
@Component
public class WorldTransitionEventListener extends WorldEventListener<WorldTransitionEvent> {
    @Override
    public void onEvent(WorldTransitionEvent event) {

    }

    @Override
    public Class<WorldTransitionEvent> getEventType() {
        return WorldTransitionEvent.class;
    }
}