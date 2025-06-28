package io.github.tml.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

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
