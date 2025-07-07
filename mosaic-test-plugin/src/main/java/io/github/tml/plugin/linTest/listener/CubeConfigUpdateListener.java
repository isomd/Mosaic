package io.github.tml.plugin.linTest.listener;

import io.github.tml.mosaic.core.event.event.CubeConfigUpdateEvent;
import io.github.tml.mosaic.core.event.listener.CubeConfigUpdateEventListener;
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.plugin.linTest.cube.ListenerTestCube;
import lombok.extern.slf4j.Slf4j;

/**
 * CubeConfig更新事件监听器
 */
@Slf4j
public class CubeConfigUpdateListener implements CubeConfigUpdateEventListener {

    private String test1 = "test1";
    private String test2 = "test2";
    private boolean test3 = false;

    @Override
    public void onEvent(CubeConfigUpdateEvent event) {
        MosaicCube mosaicCube = event.getMosaicCube();
        ListenerTestCube listenerTestCube = (ListenerTestCube) mosaicCube;
        listenerTestCube.test();
        log.info("before config: {}", event.getBefore().toString());
        log.info("after config: {}", event.getAfter().toString());
    }

    @Override
    public String getListenerName() {
        return "CubeConfigUpdateListener";
    }
}