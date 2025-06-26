package io.github.tml.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

//@DependsOn("slotManager")
@Configuration
public class TestProjectDefaultSlot{

    @Autowired
    public TestProjectDefaultSlot(SlotManager slotManager) {
        GoldenShovel.loadSlotManager(slotManager);
        GoldenShovel.slotBootStrap()
                .slotId("log.plugin.slot")
                .build();
    }
}
