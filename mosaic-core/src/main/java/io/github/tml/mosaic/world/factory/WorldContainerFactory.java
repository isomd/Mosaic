package io.github.tml.mosaic.world.factory;

import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.util.StringUtil;
import io.github.tml.mosaic.world.component.WorldComponent;
import io.github.tml.mosaic.world.container.MosaicWorldContainer;
import io.github.tml.mosaic.world.container.WorldContainer;

import java.util.ArrayList;
import java.util.List;

public class WorldContainerFactory {
    private static final GUID originalUid = CommonComponent.GuidAllocator().nextGUID();

    public static WorldContainer createWorldContainer(String name, List<Class<?>> componentClasses, Boolean isOriginal) {
        // 塞入创建的世界的components中
        List<WorldComponent> components = new ArrayList<>();
        for (Class<?> clazz : componentClasses) {
            components.add(new WorldComponent(clazz, StringUtil.getFirstLowerCase(clazz.getSimpleName())));
        }
        WorldContainer container;
        if (isOriginal){
            container = new MosaicWorldContainer(originalUid, 0, name, components);
            container.setInitialized(true);
        } else {
            container = new MosaicWorldContainer(name, 0, components);
        }
        return container;
    }

    public static WorldContainer createWorldContainer(WorldContainer worldContainer){
        WorldContainer copy = worldContainer.clone();
        copy.increaseVersion();
        return copy;
    }

    public static String getOriginalUid() {
        return originalUid.toString();
    }
}
