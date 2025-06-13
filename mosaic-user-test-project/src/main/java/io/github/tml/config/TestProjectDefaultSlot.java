package io.github.tml.config;

import io.github.tml.mosaic.GoldenShovel;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;

@DependsOn("slotManager")
@Configuration
public class TestProjectDefaultSlot {

    @PostConstruct
    public void initSlot(){
        GoldenShovel.slotBootStrap()
                .slotId("log.plugin.slot")
                .build();
    }
}
