package io.github.tml.plugin.linTest.listener;

import io.github.tml.mosaic.core.event.event.CubeConfigUpdateEvent;
import io.github.tml.mosaic.core.event.event.CubeDefinitionRegisteredEvent;
import io.github.tml.mosaic.core.event.event.MosaicEvent;
import io.github.tml.mosaic.core.event.listener.SelectiveMosaicEventListener;
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.plugin.linTest.cube.ListenerTestCube;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * CubeConfig更新事件监听器
 */
@Slf4j
public class CubeConfigUpdateListener extends SelectiveMosaicEventListener {

    private String test1 = "test1";
    private String test2 = "test2";
    private boolean test3 = false;

    @Override
    protected List<Class<? extends MosaicEvent>> getListenedEventTypes() {
        return Arrays.asList(
                CubeConfigUpdateEvent.class,
                CubeDefinitionRegisteredEvent.class
        );
    }

    @Override
    protected void onSelectiveEvent(MosaicEvent event) {
        when(event, CubeConfigUpdateEvent.class, this::handleConfigUpdate)
                .orWhen(CubeDefinitionRegisteredEvent.class, this::handleDefinitionRegistered)
                .orDefault(); // 调用父类的onUnhandledEvent方法
    }


    private void handleConfigUpdate(CubeConfigUpdateEvent event) {
        MosaicCube mosaicCube = event.getMosaicCube();
        ListenerTestCube listenerTestCube = (ListenerTestCube) mosaicCube;
        listenerTestCube.changeFlag();

        log.info("Config updated - before: {}", event.getBefore().toString());
        log.info("Config updated - after: {}", event.getAfter().toString());
    }

    private void handleDefinitionRegistered(CubeDefinitionRegisteredEvent event) {
        log.info("Cube definition registered: {}", event.getCubeId());
    }

    @Override
    public String getListenerName() {
        return "CubeConfigUpdateListener";
    }
}