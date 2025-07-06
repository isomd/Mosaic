package io.github.tml.mosaic.config.mosaic;

import io.github.tml.mosaic.core.event.DefaultMosaicEventBroadcaster;
import io.github.tml.mosaic.core.event.MosaicEventBroadcaster;
import io.github.tml.mosaic.core.event.listener.MosaicEventListener;
import io.github.tml.mosaic.core.world.event.WorldEventBroadcaster;
import io.github.tml.mosaic.core.world.event.listener.WorldEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MosaicEventConfig {
    
    @Bean
    public MosaicEventBroadcaster cubeEventBroadcaster(List<MosaicEventListener> mosaicEventListeners) {
        DefaultMosaicEventBroadcaster broadcaster = DefaultMosaicEventBroadcaster.broadcaster();
        // 注册默认监听器
        for (MosaicEventListener mosaicEventListener : mosaicEventListeners) {
            broadcaster.registerListener(mosaicEventListener);
        }

        return broadcaster;
    }

    @Bean
    public WorldEventBroadcaster worldEventBroadcaster(List<WorldEventListener> mosaicEventListeners) {
        WorldEventBroadcaster broadcaster = new WorldEventBroadcaster();
        // 注册默认监听器
        for (WorldEventListener mosaicEventListener : mosaicEventListeners) {
            broadcaster.registerListener(mosaicEventListener);
        }
        return broadcaster;
    }
}