package io.github.tml.mosaic.config.mosaic;

import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.util.StringUtil;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MosaicComponentConfig {
    @Getter
    private static final List<Class<?>> componentClasses = List.of(CubeContext.class, SlotManager.class, CubeActuatorProxy.class, HotSwapContext.class);
}
