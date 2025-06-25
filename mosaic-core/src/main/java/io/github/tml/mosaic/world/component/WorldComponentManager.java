package io.github.tml.mosaic.world.component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WorldComponentManager {
    private ConcurrentHashMap<Class<?>, String> worldComponents;

    public WorldComponentManager(List<WorldComponent> components) {
        this.worldComponents = components.stream()
                .collect(ConcurrentHashMap::new, (map, component) -> map.put(component.getClazz(), component.getComponentClassName()), ConcurrentHashMap::putAll);
    }

    public String getComponentName(Class<?> clazz, String worldUid) {
        return worldComponents.get(clazz) + worldUid;
    }

    public void put(WorldComponent component) {
        worldComponents.put(component.getClazz(),component.getComponentClassName());
    }
}