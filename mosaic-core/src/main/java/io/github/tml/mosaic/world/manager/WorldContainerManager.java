package io.github.tml.mosaic.world.manager;

import io.github.tml.mosaic.world.WorldContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldContainerManager {
    protected ConcurrentHashMap<String, WorldContainer> worldContainerMap;

    public WorldContainerManager() {
        this.worldContainerMap = new ConcurrentHashMap<>();
    }

    public WorldContainerManager(ConcurrentHashMap<String, WorldContainer> worldContainerMap) {
        this.worldContainerMap = worldContainerMap;
    }

    public void addWorldContainer(WorldContainer worldContainer) {
        this.worldContainerMap.put(worldContainer.getName(), worldContainer);
    }

    public WorldContainer getWorldContainer(String name) {
        return this.worldContainerMap.get(name);
    }

    public void removeWorldContainer(String name) {
        this.worldContainerMap.remove(name);
    }
}
