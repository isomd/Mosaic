package io.github.tml.mosaic.world.factory;

import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.config.CubeConfig;
import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.util.StringUtil;
import io.github.tml.mosaic.world.component.WorldComponent;
import io.github.tml.mosaic.world.container.MosaicWorldContainer;
import io.github.tml.mosaic.world.container.WorldContainer;

import java.util.ArrayList;
import java.util.List;

public class WorldContainerFactory {
    private static final GUID originalUid = CommonComponent.GuidAllocator().nextGUID();

    public static WorldContainer createWorldContainer(String name, List<Class<?>> classes, Boolean isOriginal) {
        // 塞入创建的世界的components中
        List<WorldComponent> components = new ArrayList<>();
        for (Class<?> clazz : classes) {
            components.add(new WorldComponent(clazz, StringUtil.getFirstLowerCase(clazz.getSimpleName())));
        }
        return isOriginal ? new MosaicWorldContainer(originalUid, 0, name, components) : new MosaicWorldContainer(name, 0, components);
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
