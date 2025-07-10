package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.core.execption.HotSwapException;
import io.github.tml.mosaic.domain.hotswap.HotSwapDomain;
import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.entity.dto.HotSwapPointDTO;
import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.req.HotSwapPointRequest;
import io.github.tml.mosaic.entity.resp.CreateHotSwapPointResp;
import io.github.tml.mosaic.entity.resp.CreateSlotResp;
import io.github.tml.mosaic.hotSwap.model.MethodMap;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import io.github.tml.mosaic.service.HotSwapService;
import io.github.tml.mosaic.service.SlotService;
import io.github.tml.mosaic.util.CodeTemplateUtil;
import io.github.tml.mosaic.util.HotSwapUtil;
import io.github.tml.mosaic.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
@Slf4j
@Service
public class HotSwapServiceImpl implements HotSwapService {

    @Autowired
    private HotSwapDomain hotSwapDomain;
    @Autowired
    private SlotService slotService;

    @Override
    public R<?> getClassStrByClassFullName(String classFullName) {
        try {
            return R.success(hotSwapDomain.getProxyCodeByClassFullName(classFullName));
        }catch (Exception e){
            log.error(e.getMessage());
            return R.error(e.getMessage());
        }
    }

    @Override
    public R<?> proxyCode(HotSwapDTO dto) {
        try {
            return R.success(hotSwapDomain.proxyCodeByFullName(dto));
        }catch (Exception e){
            log.error(e.getMessage());
            return R.error(e.getMessage());
        }
    }

    @Override
    public R<?> createHotSwapPoint(HotSwapPointRequest dto) {

        //1.创建槽点
        AppendSlotReq slotReq = new AppendSlotReq();
        BeanUtils.copyProperties(dto, slotReq);
        slotReq.setSetupFlag(true);

        try {
            CreateSlotResp res = slotService.createOrSetupSlot(slotReq);

            CreateHotSwapPointResp resp = new CreateHotSwapPointResp();

            if(res.isSuccess()){
                //2.热更新代码
                HotSwapContext.InsertType type = hotSwapDomain.matchType(dto.getChangeType());
                String needInsertCode = CodeTemplateUtil.buildCodeTemplate(dto.getSlotId(),dto.getArgs());
                HotSwapDTO hotSwapDTO = new HotSwapDTO();
                BeanUtils.copyProperties(dto, hotSwapDTO);
                hotSwapDTO.setType(type);
                hotSwapDTO.setProxyCode(needInsertCode);
                //2.1 获取类当前源代码
                String oldSourceCode = hotSwapDomain.getProxyCodeByClassFullName(hotSwapDTO.getClassName());
                //2.2 热更新
                String proxySourceCode = hotSwapDomain.proxyCodeByFullName(hotSwapDTO);

                //2.2 获取当前更新的方法 为下文热更新点做准备
                MethodMap methodMapVO = HotSwapUtil.extractMethodByLine(oldSourceCode, dto.getTargetLine());
                String method = methodMapVO.getMethodName();
                String oldSourceMethodCode = methodMapVO.getMethodCode();

                String proxyMethodCode = HotSwapUtil.extractMethodSource(proxySourceCode, method);
                //3.生成热更新点
                HotSwapPointDTO hotSwapPointDTO = HotSwapPointDTO.convert(dto, oldSourceMethodCode, proxyMethodCode, type,method);
                hotSwapDomain.generateHotSwapPoint(hotSwapPointDTO);
                resp.setCode(proxySourceCode);
            }
            return R.success(resp);
        }catch (Exception e){
            log.error(e.getMessage());
            return R.error(e.getMessage());
        }
    }

    @Override
    public R<?> getHotSwapPoints(String classFullName) {

        try {
            List<HotSwapPoint> hotSwapPoints = hotSwapDomain.getHotSwapPoints(classFullName);
            return R.success(Optional.ofNullable(hotSwapPoints).orElse(new ArrayList<>()));
        }catch (Exception e){
            log.error(e.getMessage());
            return R.error(e.getMessage());
        }

    }

    @Override
    public R<?> rollBackClassHotSwapPoint(String className, String methodName) {
        try {
            return R.success(hotSwapDomain.rollBackHotSwapPoint(className, methodName));
        }catch (Exception e){
            log.error(e.getMessage());
            return R.error(e.getMessage());
        }
    }

}