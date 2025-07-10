package io.github.tml.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;

@DependsOn("mosaicInitConfig")
@Configuration
public class TestProjectDefaultSlot{
    @PostConstruct
    public void init() {

//        GoldenShovel.slotBootStrap()
//                .slotId("log.plugin.slot")
//                .build();
    }
}
