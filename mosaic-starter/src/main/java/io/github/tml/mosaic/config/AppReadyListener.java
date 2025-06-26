package io.github.tml.mosaic.config;

import io.github.tml.mosaic.world.construct.MosaicWorld;
import io.github.tml.mosaic.world.replace.ComponentReplace;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

@Component
public class AppReadyListener {
    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MosaicWorld mosaicWorld;
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        mosaicWorld.setReplaceList(new ArrayList<>(applicationContext.getBeansOfType(ComponentReplace.class).values()));
    }
}