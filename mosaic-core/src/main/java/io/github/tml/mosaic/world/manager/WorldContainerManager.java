package io.github.tml.mosaic.world.manager;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.world.container.MosaicWorldContainer;
import io.github.tml.mosaic.world.container.WorldContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WorldContainerManager {
    protected ConcurrentHashMap<GUID, WorldContainer> worldContainerMap;

    public WorldContainerManager() {
        this.worldContainerMap = new ConcurrentHashMap<>();
    }

    public WorldContainerManager(ConcurrentHashMap<GUID, WorldContainer> worldContainerMap) {
        this.worldContainerMap = worldContainerMap;
    }

    public void addWorldContainer(WorldContainer worldContainer) {
        this.worldContainerMap.put(worldContainer.getId(), worldContainer);
    }

    public WorldContainer getWorldContainer(GUID id) {
        return this.worldContainerMap.get(id);
    }

    public WorldContainer removeWorldContainer(GUID id) {
        return this.worldContainerMap.remove(id);
    }

    public List<WorldContainer> getAllWorldContainer() {
        return new ArrayList<>(this.worldContainerMap.values());
    }
}
