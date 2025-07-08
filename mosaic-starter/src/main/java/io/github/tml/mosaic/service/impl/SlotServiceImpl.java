package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.convert.SlotConvert;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.domain.cube.CubeDomain;
import io.github.tml.mosaic.domain.slot.SlotDomain;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.dto.SlotDTO;
import io.github.tml.mosaic.entity.dto.SlotSetupDTO;
import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.resp.CreateSlotResp;
import io.github.tml.mosaic.entity.vo.slot.SlotVO;
import io.github.tml.mosaic.service.SlotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotDomain slotDomain;

    @Resource
    private CubeDomain cubeDomain;

    @Override
    public List<SlotVO> getSlotList() {
        List<SlotDTO> slotList = slotDomain.getSlotList();
        return slotList.stream().map(SlotConvert::convert2VO).collect(Collectors.toList());
    }

    @Override
    public CreateSlotResp createOrSetupSlot(AppendSlotReq appendSlotReq) {
        String slotId = appendSlotReq.getSlotId();

        CreateSlotResp createSlotResp = new CreateSlotResp();
        String errorMessage = null;
        boolean isSuccess = true;
        // 创建槽
        if (!slotDomain.hasSlot(slotId)) {
            if (slotDomain.createSlot(slotId).isEmpty()) {
                log.error("create slot failed, slotId:{}", slotId);
                errorMessage = "create slot failed";
                isSuccess = false;
            }
        }

        // 安装槽信息
        if(appendSlotReq.isSetupFlag()){
            SlotSetupDTO slotSetupDTO = SlotConvert.appendSlotReqConvert2SetupDTO(appendSlotReq);
            Optional<CubeDTO> cubeOptional = cubeDomain.getCubeById(slotSetupDTO.getCubeId().toString());

            if(cubeOptional.isEmpty()) {
                throw new CubeException("cube id not exist");
            }

            try {
                if (!slotDomain.setupSlot(slotSetupDTO)) {
                    errorMessage = "setup slot failed";
                    isSuccess = false;
                }
            }catch (Exception e){
                log.error("setup slot failed, slotId:{}, error:{}", slotId, e.getMessage());
                errorMessage = e.getMessage();
                isSuccess = false;
            }
        }

        createSlotResp.setSuccess(isSuccess);
        createSlotResp.setErrorMsg(errorMessage);
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
