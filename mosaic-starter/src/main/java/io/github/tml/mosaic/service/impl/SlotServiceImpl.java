package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.convert.SlotConvert;
import io.github.tml.mosaic.domain.SlotDomain;
import io.github.tml.mosaic.entity.dto.SlotDTO;
import io.github.tml.mosaic.entity.dto.SlotSetupDTO;
import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.vo.slot.SlotVO;
import io.github.tml.mosaic.service.SlotService;
import io.github.tml.mosaic.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotDomain slotDomain;

    @Override
    public R<?> getSlotList() {
        List<SlotDTO> slotList = slotDomain.getSlotList();
        List<SlotVO> slotVOS = slotList.stream().map(SlotConvert::convert2VO).collect(Collectors.toList());
        return R.success(Map.of(
                "slotList", slotVOS
        ));
    }

    @Override
    public R<?> createOrSetupSlot(AppendSlotReq appendSlotReq) {
        String slotId = appendSlotReq.getSlotId();
        // 创建槽
        if (!slotDomain.hasSlot(slotId)) {
            if (slotDomain.createSlot(slotId).isEmpty()) {
                log.error("create slot failed, slotId:{}", slotId);
                return R.error("create slot failed");
            }
        }

        // 安装槽信息
        if(appendSlotReq.isSetupFlag()){
            SlotSetupDTO slotSetupDTO = SlotConvert.appendSlotReqConvert2SetupDTO(appendSlotReq);
            if (!slotDomain.setupSlot(slotSetupDTO)) {
                log.error("setup slot failed, slotId:{}", slotId);
                return R.error("setup slot failed");
            }
        }
        return R.success();
    }

    @Override
    public R<?> unSetupSlot(String slotId) {
        if (slotDomain.unSetupSlot(slotId)) {
            return R.success();
        }else{
            return R.error("unSetup slot failed");
        }
    }

    @Override
    public R<?> deleteSlot(String slotId) {
        slotDomain.removeSlot(slotId);
        return R.success();
    }
}
