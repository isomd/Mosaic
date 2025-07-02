package io.github.tml.mosaic.world.component;

import io.github.tml.mosaic.core.tools.guid.GUID;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WorldComponentManager {
    private ConcurrentHashMap<Class<?>, String> worldComponents;

    private GUID guid;

    public WorldComponentManager(List<WorldComponent> components, GUID guid) {
        this.worldComponents = components.stream()
                .collect(ConcurrentHashMap::new, (map, component) -> map.put(component.getClazz(), component.getComponentClassName()), ConcurrentHashMap::putAll);
        this.guid = guid;
    }

    public String getComponentName(Class<?> clazz) {
        if(worldComponents.containsKey(clazz)) return worldComponents.get(clazz) + guid.toString();

        throw new RuntimeException("Component not found");
    }

    public void put(WorldComponent component) {
        worldComponents.put(component.getClazz(),component.getComponentClassName());
    }

    public Boolean contains(Class<?> clazz) {
        return worldComponents.containsKey(clazz);
    }
}