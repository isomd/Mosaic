package io.github.tml.mosaic.world.component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WorldComponentManager {
    private ConcurrentHashMap<String, WorldComponent> worldComponents;

    public WorldComponentManager(List<WorldComponent> components) {
        this.worldComponents = components.stream()
                .collect(ConcurrentHashMap::new, (map, component) -> map.put(component.getName(), component), ConcurrentHashMap::putAll);
    }

    public WorldComponent get(String name) {
        return worldComponents.get(name);
    }

    public void put(WorldComponent component) {
        worldComponents.put(component.getName(),component);
    }

    public Object getComponent(String name) {
        return worldComponents.get(name).getComponent();
    }
}