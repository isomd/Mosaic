package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.convert.SlotConvert;
import io.github.tml.mosaic.domain.SlotDomain;
import io.github.tml.mosaic.entity.dto.SlotDTO;
import io.github.tml.mosaic.entity.dto.SlotSetupDTO;
import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.resp.CreateSlotResp;
import io.github.tml.mosaic.entity.vo.slot.SlotVO;
import io.github.tml.mosaic.service.SlotService;
import io.github.tml.mosaic.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotDomain slotDomain;

    @Override
    public List<SlotVO> getSlotList() {
        List<SlotDTO> slotList = slotDomain.getSlotList();
        return slotList.stream().map(SlotConvert::convert2VO).collect(Collectors.toList());
    }

    @Override
    public CreateSlotResp createOrSetupSlot(AppendSlotReq appendSlotReq) {
        String slotId = appendSlotReq.getSlotId();

        CreateSlotResp createSlotResp = new CreateSlotResp();
        // 创建槽
        if (!slotDomain.hasSlot(slotId)) {
            if (slotDomain.createSlot(slotId).isEmpty()) {
                log.error("create slot failed, slotId:{}", slotId);
                createSlotResp.setErrorMsg("create slot failed");
                createSlotResp.setSuccess(false);
                return createSlotResp;
            }
        }

        // 安装槽信息
        if(appendSlotReq.isSetupFlag()){
            SlotSetupDTO slotSetupDTO = SlotConvert.appendSlotReqConvert2SetupDTO(appendSlotReq);
            if (!slotDomain.setupSlot(slotSetupDTO)) {
                createSlotResp.setErrorMsg("create slot failed");
                createSlotResp.setSuccess(false);
                return createSlotResp;
            }
        }

        createSlotResp.setSuccess(true);
        return createSlotResp;
    }

    @Override
    public boolean unSetupSlot(String slotId) {
        return slotDomain.unSetupSlot(slotId);
    }

    @Override
    public boolean deleteSlot(String slotId) {
        slotDomain.removeSlot(slotId);
        return true;
    }
}
