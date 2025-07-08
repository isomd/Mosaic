package io.github.tml.mosaic.world.component;

import java.util.Map;

public interface ComponentReplacer {
    void replaceComponent(Map<Class<?>, String> newComponentNames, Map<Class<?>, String> oldComponentNames);

    void replaceComponent(Map<Class<?>, String> newComponentNames);
}
