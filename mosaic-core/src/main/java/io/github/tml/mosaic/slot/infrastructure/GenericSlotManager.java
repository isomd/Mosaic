package io.github.tml.mosaic.slot.infrastructure;

import io.github.tml.mosaic.core.tools.copy.DeepCopyUtil;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.slot.Slot;
import io.github.tml.mosaic.world.container.MosaicWorldContainer;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

public class GenericSlotManager implements SlotManager {

    private Map<GUID, Slot> slotMap = new HashMap<>();

//    private final static GenericSlotManager MANAGER  = new GenericSlotManager();

    public GenericSlotManager(){
    }

    public static GenericSlotManager manager(){
        return new GenericSlotManager();
    }

    @Override
    public List<Slot> getSlotList() {
        return new ArrayList<>(slotMap.values());
    }

    @Override
    public Slot getSlot(GUID slotId) {
        return slotMap.get(slotId);
    }

    @Override
    public Slot getSlot(String slotId) {
        DotNotationId id = new DotNotationId(slotId);
        return getSlot(id);
    }

    @Override
    public boolean registerSlot(Slot slot) {
        if(slotMap.containsKey(slot.getId())){
            return false;
        }
        slotMap.put(slot.getId(), slot);
        return true;
    }

    @Override
    public void removeSlot(GUID slotId) {
        slotMap.remove(slotId);
    }

    @Override
    public void removeSlot(String slotId) {
        DotNotationId id = new DotNotationId(slotId);
        removeSlot(id);
    }

    @Override
    public boolean setup(GUID slotId, Slot.SetupCubeInfo setupCubeInfo) {
        Slot slot = getSlot(slotId);
        if(Objects.nonNull(slot)){
            return slot.Setup(setupCubeInfo);
        }
        return false;
    }

    public boolean unSetup(GUID slotId) {
        Slot slot = getSlot(slotId);
        if(Objects.nonNull(slot)){
            return slot.UnSetup();
        }
        return false;
    }
}
