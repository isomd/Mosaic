package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.util.R;

public interface SlotService {

    R<?> getSlotList();

    R<?> createOrSetupSlot(AppendSlotReq appendSlotReq);

    R<?> unSetupSlot(String slotId);

    R<?> deleteSlot(String slotId);
}
