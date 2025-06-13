package io.github.tml.config;

import io.github.tml.mosaic.GoldenShovel;

public class TestProjectDefaultSlot {

    static {
        GoldenShovel.slotBootStrap()
                .slotId("log.plugin.slot")
                .build();
    }
}
