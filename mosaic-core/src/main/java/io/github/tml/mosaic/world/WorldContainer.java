package io.github.tml.mosaic.world;

import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import lombok.Data;

@Data
public abstract class WorldContainer {
    private String name;

    private String worldPath;

    protected SlotManager slotManager;

    protected CubeContext cubeContext;

    public WorldContainer(String name, String worldPath, SlotManager slotManager, CubeContext cubeContext) {
        this.name = name;
        this.worldPath = worldPath;
        this.slotManager = slotManager;
        this.cubeContext = cubeContext;
    }


}
