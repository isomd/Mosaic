package io.github.tml.mosaic.core.world.event.listener;

import io.github.tml.mosaic.core.world.event.event.AfterWorldTransitionEvent;
import org.springframework.stereotype.Component;

// TODO: 处理世界切换事件
@Component
public class AfterWorldTransitionEventListener extends WorldEventListener<AfterWorldTransitionEvent> {
    @Override
    public void onEvent(AfterWorldTransitionEvent event) {

    }

    @Override
    public Class<AfterWorldTransitionEvent> getEventType() {
        return AfterWorldTransitionEvent.class;
    }
}