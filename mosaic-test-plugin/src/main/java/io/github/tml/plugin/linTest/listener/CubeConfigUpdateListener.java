package io.github.tml.plugin.linTest.listener;

import io.github.tml.mosaic.core.event.event.CubeConfigUpdateEvent;
import io.github.tml.mosaic.core.event.listener.CubeConfigUpdateEventListener;
import lombok.extern.slf4j.Slf4j;

/**
 * CubeConfig更新事件监听器
 */
@Slf4j
public class CubeConfigUpdateListener implements CubeConfigUpdateEventListener {

    @Override
    public void onEvent(CubeConfigUpdateEvent event) {
        log.info("before config: {}", event.getBefore().toString());
        log.info("after config: {}", event.getAfter().toString());
    }

    @Override
    public String getListenerName() {
        return "CubeConfigUpdateListener";
    }
}