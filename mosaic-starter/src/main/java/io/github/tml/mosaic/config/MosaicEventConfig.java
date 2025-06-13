package io.github.tml.mosaic.config;

import io.github.tml.mosaic.core.event.CubeEventBroadcaster;
import io.github.tml.mosaic.core.event.DefaultCubeEventBroadcaster;
import io.github.tml.mosaic.core.event.listener.CubeEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MosaicEventConfig {
    
    @Bean
    public CubeEventBroadcaster cubeEventBroadcaster(List<CubeEventListener> cubeEventListeners) {
        DefaultCubeEventBroadcaster broadcaster = DefaultCubeEventBroadcaster.broadcaster();
        // 注册默认监听器
        for (CubeEventListener cubeEventListener : cubeEventListeners) {
            broadcaster.registerListener(cubeEventListener);
        }
        return broadcaster;
    }
}