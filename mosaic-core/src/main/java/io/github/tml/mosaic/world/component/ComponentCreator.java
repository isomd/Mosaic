package io.github.tml.mosaic.world.component;

import io.github.tml.mosaic.world.WorldContainer;

public interface ComponentCreator {
    void createComponents(WorldContainer worldContainer);

    void createComponents(WorldContainer oldWorldContainer, WorldContainer newWorldContainer);
}
