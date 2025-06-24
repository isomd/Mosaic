package io.github.tml.mosaic.world.component;

import io.github.tml.mosaic.world.container.WorldContainer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldComponentManager {
    private ConcurrentHashMap<String, WorldComponent> worldComponents;

    public WorldComponentManager(List<WorldComponent> components) {
        this.worldComponents = components.stream()
                .collect(ConcurrentHashMap::new, (map, component) -> map.put(component.getName(), component), ConcurrentHashMap::putAll);
    }

    public Object get(String name) {
        return worldComponents.get(name);
    }

    public void put(WorldComponent component) {
        worldComponents.put(component.getName(),component);
    }
}