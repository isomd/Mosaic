package io.github.tml.mosaic.slot.infrastructure;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.slot.Slot;

import java.util.List;

/**
 * 槽管理器，复杂对槽进行管理
 */
public interface SlotManager {

    List<Slot> getSlotList();

    Slot getSlot(GUID slotId);

    Slot getSlot(String slotId);

    boolean registerSlot(Slot slot);

    void removeSlot(GUID slotId);

    void removeSlot(String slotId);

    boolean setup(GUID slotId, Slot.SetupCubeInfo setupCubeInfo);

    boolean unSetup(GUID slotId);
}
