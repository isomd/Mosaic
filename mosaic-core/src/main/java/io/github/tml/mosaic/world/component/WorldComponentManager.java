package io.github.tml.mosaic.world.component;

import io.github.tml.mosaic.world.container.WorldContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldComponentManager {
    private Map<String, Object> worldComponents;

    public WorldComponentManager() {
        worldComponents = new ConcurrentHashMap<>();
    }

    public void init(WorldContainer worldContainer){
        worldComponents.put("slotManager", worldContainer.getSlotManager());
        worldComponents.put("cubeContext", worldContainer.getCubeContext());
    }

    public Object get(String name) {
        return worldComponents.get(name);
    }
}