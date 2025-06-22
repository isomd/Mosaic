package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.domain.HotSwapDomain;
import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.entity.dto.HotSwapPointDTO;
import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.req.HotSwapPointRequest;
import io.github.tml.mosaic.entity.resp.CreateHotSwapPointResp;
import io.github.tml.mosaic.entity.resp.CreateSlotResp;
import io.github.tml.mosaic.service.HotSwapService;
import io.github.tml.mosaic.service.SlotService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
@Service
public class HotSwapServiceImpl implements HotSwapService {

    @Autowired
    private HotSwapDomain domain;
    @Autowired
    private SlotService slotService;

    @Override
    public String getClassStrByClassFullName(String classFullName) {
        return domain.getProxyCodeByClassFullName(classFullName);
    }

    @Override
    public String proxyCode(HotSwapDTO dto) {
        return domain.proxyCodeByFullName(dto);
    }

    @Override
    public CreateHotSwapPointResp createHotSwapPoint(HotSwapPointRequest dto) {

        AppendSlotReq slotReq = new AppendSlotReq();
        BeanUtils.copyProperties(dto, slotReq);

        CreateSlotResp res = slotService.createOrSetupSlot(slotReq);

        HotSwapPointDTO hotSwapPointDTO = new HotSwapPointDTO();
        BeanUtils.copyProperties(dto, hotSwapPointDTO);

        CreateHotSwapPointResp resp = new CreateHotSwapPointResp();

        if(res.isSuccess()){
            domain.generateHotSwapPoint(hotSwapPointDTO);

            HotSwapDTO hotSwapDTO = new HotSwapDTO();
            BeanUtils.copyProperties(dto, hotSwapDTO);
            String proxy = domain.proxyCodeByFullName(hotSwapDTO);
            resp.setCode(proxy);
        }else{
            resp.setErrorMsg(res.getErrorMsg());
        }

        return resp;
    }

}