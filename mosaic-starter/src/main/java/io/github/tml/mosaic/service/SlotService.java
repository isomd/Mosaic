package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.resp.CreateSlotResp;
import io.github.tml.mosaic.entity.vo.slot.SlotVO;
import io.github.tml.mosaic.util.R;

import java.util.List;

public interface SlotService {

    List<SlotVO> getSlotList();

    CreateSlotResp createOrSetupSlot(AppendSlotReq appendSlotReq);

    boolean unSetupSlot(String slotId);

    boolean deleteSlot(String slotId);
}
