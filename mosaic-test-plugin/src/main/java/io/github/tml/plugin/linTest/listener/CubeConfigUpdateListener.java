package io.github.tml.plugin.linTest.listener;

import io.github.tml.mosaic.core.event.event.MosaicEvent;
import io.github.tml.mosaic.core.event.listener.MosaicCommonListenerEventListener;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class CubeConfigUpdateListener implements MosaicCommonListenerEventListener {

    @Override
    public void onEvent(MosaicEvent event) {
        System.out.println(event.toString());
    }

    @Override
    public String getListenerName() {
        return "CubeConfigUpdateListener";
    }
}