package io.github.tml.mosaic.world.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldComponentManager {
    private Map<String, Object> worldComponents;

    public WorldComponentManager() {
        worldComponents = new ConcurrentHashMap<>();
    }

    protected void registry(String name, Object component) {
        worldComponents.put(name, component);
    }

    public Object get(String name) {
        return worldComponents.get(name);
    }
}