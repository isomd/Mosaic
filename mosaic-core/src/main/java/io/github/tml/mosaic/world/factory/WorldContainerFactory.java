package io.github.tml.mosaic.world.factory;

import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.world.WorldContainer;

import java.util.List;

public class WorldContainerFactory {
    private static final GUID originalUid = CommonComponent.GuidAllocator().nextGUID();

    public static WorldContainer createWorldContainer(String name, List<Class<?>> componentClasses, Boolean isOriginal) {
        // 塞入创建的世界的components中
        return isOriginal ? new WorldContainer(originalUid, name, componentClasses) : new WorldContainer(name, componentClasses);
    }

    public static WorldContainer createWorldContainer(WorldContainer worldContainer){
        return worldContainer.clone();
    }

    public static String getOriginalUid() {
        return originalUid.toString();
    }
}
