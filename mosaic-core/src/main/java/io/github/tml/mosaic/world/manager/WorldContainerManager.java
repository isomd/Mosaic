package io.github.tml.mosaic.world.manager;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.world.container.MosaicWorldContainer;
import io.github.tml.mosaic.world.container.WorldContainer;

import java.util.concurrent.ConcurrentHashMap;

public class WorldContainerManager {
    protected ConcurrentHashMap<GUID, WorldContainer> worldContainerMap;

    public static final WorldContainer ORIGINAL = new MosaicWorldContainer("original", -1, new GenericSlotManager(), new ClassPathCubeContext());

    public WorldContainerManager() {
        this.worldContainerMap = new ConcurrentHashMap<>();
    }

    public WorldContainerManager(ConcurrentHashMap<GUID, WorldContainer> worldContainerMap) {
        this.worldContainerMap = worldContainerMap;
    }

    public void init(){
        worldContainerMap.put(ORIGINAL.getId(), ORIGINAL);
    }

    public void addWorldContainer(WorldContainer worldContainer) {
        this.worldContainerMap.put(worldContainer.getId(), worldContainer);
    }

    public WorldContainer getWorldContainer(GUID id) {
        return this.worldContainerMap.get(id);
    }

    public void removeWorldContainer(GUID id) {
        this.worldContainerMap.remove(id);
    }

    public static WorldContainer getOriginalWorldContainer() {
        return ORIGINAL;
    }
}
