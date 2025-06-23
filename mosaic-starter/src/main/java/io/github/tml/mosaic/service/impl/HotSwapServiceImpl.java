package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.domain.HotSwapDomain;
import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.entity.dto.HotSwapPointDTO;
import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.req.HotSwapPointRequest;
import io.github.tml.mosaic.entity.resp.CreateHotSwapPointResp;
import io.github.tml.mosaic.entity.resp.CreateSlotResp;
import io.github.tml.mosaic.entity.vo.hotSwap.MethodMapVO;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import io.github.tml.mosaic.service.HotSwapService;
import io.github.tml.mosaic.service.SlotService;
import io.github.tml.mosaic.util.CodeTemplateUtil;
import io.github.tml.mosaic.util.HotSwapUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
@Service
public class HotSwapServiceImpl implements HotSwapService {

    @Autowired
    private HotSwapDomain hotSwapDomain;
    @Autowired
    private SlotService slotService;

    @Override
    public String getClassStrByClassFullName(String classFullName) {
        return hotSwapDomain.getProxyCodeByClassFullName(classFullName);
    }

    @Override
    public String proxyCode(HotSwapDTO dto) {
        return hotSwapDomain.proxyCodeByFullName(dto);
    }

    @Override
    public CreateHotSwapPointResp createHotSwapPoint(HotSwapPointRequest dto) {

        //1.创建槽点
        AppendSlotReq slotReq = new AppendSlotReq();
        BeanUtils.copyProperties(dto, slotReq);

        CreateSlotResp res = slotService.createOrSetupSlot(slotReq);

        CreateHotSwapPointResp resp = new CreateHotSwapPointResp();

        if(res.isSuccess()){
            //2.热更新代码
            HotSwapContext.InsertType type = hotSwapDomain.matchType(dto.getChangeType());
            String needInsertCode = "";
            if (HotSwapContext.InsertType.INSERT_AFTER.equals(type)) {
                needInsertCode = CodeTemplateUtil.generateCubeTemplateBySlotName(dto.getSlotId());
            }else{
                needInsertCode = CodeTemplateUtil.generateCubeTemplateByParams(dto.getArgs());
            }
            HotSwapDTO hotSwapDTO = new HotSwapDTO();
            BeanUtils.copyProperties(dto, hotSwapDTO);
            hotSwapDTO.setType(type);
            hotSwapDTO.setProxyCode(needInsertCode);
            String proxySourceCode = hotSwapDomain.proxyCodeByFullName(hotSwapDTO);

            //2.1 获取类当前源代码
            String oldSourceCode = hotSwapDomain.getProxyCodeByClassFullName(hotSwapDTO.getClassName());
            //2.2 获取当前更新的方法 为下文热更新点做准备
            MethodMapVO methodMapVO = HotSwapUtil.extractMethodByLine(oldSourceCode, dto.getTargetLine());
            String method = methodMapVO.getMethodName();
            String oldSourceMethodCode = methodMapVO.getMethodCode();

            String proxyMethodCode = HotSwapUtil.extractMethodSource(proxySourceCode, method);
            //3.生成热更新点
            HotSwapPointDTO hotSwapPointDTO = HotSwapPointDTO.convert(dto, oldSourceMethodCode, proxyMethodCode, type);
            hotSwapDomain.generateHotSwapPoint(hotSwapPointDTO);
            resp.setCode(proxySourceCode);
        }else{
            resp.setErrorMsg(res.getErrorMsg());
        }
        return resp;
    }

    @Override
    public List<HotSwapPoint> getHotSwapPoints(String classFullName) {

        List<HotSwapPoint> hotSwapPoints = hotSwapDomain.getHotSwapPoints(classFullName);

        return Optional.ofNullable(hotSwapPoints).orElse(new ArrayList<>());
    }

    @Override
    public String rollBackClassHotSwapPoint(String className, String methodName) {
        return hotSwapDomain.rollBackHotSwapPoint(className, methodName);
    }

}