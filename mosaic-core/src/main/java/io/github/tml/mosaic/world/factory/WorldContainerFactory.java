package io.github.tml.mosaic.world.factory;

import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.world.MosaicWorldContainer;
import io.github.tml.mosaic.world.WorldContainer;
import io.github.tml.mosaic.world.construct.WorldConstruct;

public class WorldContainerFactory {
    public static WorldContainer createWorldContainer(String name) {
        SlotManager slotManager = new GenericSlotManager();
        CubeContext cubeContext = new ClassPathCubeContext();
        return new MosaicWorldContainer(name, 0, slotManager, cubeContext);
    }

    public static WorldContainer createWorldContainer(WorldContainer worldContainer){
        WorldContainer copy = worldContainer.clone();
        copy.increaseVersion();
        return copy;
    }
}
