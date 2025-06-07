package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.slot.Slot;

/**
 * 方块执行器
 */
public interface CubeActuator {
    // TODO execute方法改写
    Object execute(Slot slot, Object...args);
}
