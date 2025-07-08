package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.config.mosaic.MosaicHotSwapConfig;
import io.github.tml.mosaic.core.execption.HotSwapException;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.entity.dto.HotSwapPointDTO;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import io.github.tml.mosaic.hotSwap.model.ChangeMethodRecord;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import io.github.tml.mosaic.util.CodeTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author welsir
 * @description :
 * @date 2025/6/22
 */
@Service
@Slf4j
public class HotSwapDomain {

    @Autowired
    MosaicHotSwapConfig hotSwapConfig;
    @Autowired
    HotSwapContext context;

    /**
     *  根据参数进行代码增强并返回源码字符串
     * @param dto
     * @return
     */
    public String proxyCodeByFullName(HotSwapDTO dto) {

        try {
            String code = getProxyCodeByClassFullName(dto.getClassName());
            //1.构建增强代码
            String proxy = context.generateProxyCode(
                    code,
                    dto.getTargetLine(),
                    dto.getType(),
                    dto::getProxyCode,
                    Set.of(CodeTemplateUtil.getCubeImportPath())
            );
            //2.热部署注入
//            AgentServerResp resp = NotifyAgentBySocket(proxy, dto.getClassName());
            context.notifyAgentBySocket(proxy, dto.getClassName());
            //3.更新/新增缓存
            context.putClassProxyCode(dto.getClassName(), proxy);
            return proxy;
        }catch (Exception e){
            throw new HotSwapException("热更新代码失败: "+e.getMessage());
        }
    }

    public String getProxyCodeByClassFullName(String fullName) {

        if(context.ProxyCodeContainsKey(fullName)) {
            return context.getProxyCode(fullName);
        }

        String code = context.decompileClassByClassName(fullName);
        context.putClassProxyCode(fullName,code);

        return code;
    }

    public HotSwapPoint generateHotSwapPoint(HotSwapPointDTO pointDTO) {

        ChangeMethodRecord record = new ChangeMethodRecord(pointDTO.getMethodName(),pointDTO.getOldSourceCode(),pointDTO.getNewSourceCode());

        LocalDateTime now = LocalDateTime.now();

        HotSwapPoint hotSwapPoint = new HotSwapPoint(new GUUID(pointDTO.getClassName()+pointDTO.getMethodName()+ now).toString(), now,pointDTO.getClassName(),pointDTO.getChangeType(),record);

        setHotSwapPoint(hotSwapPoint);

        return hotSwapPoint;

    }

    public List<HotSwapPoint> getHotSwapPoints(String className) {
        List<HotSwapPoint> points = context.getHotSwapPointsByClassName(className);
        return Optional.ofNullable(points).orElse(new ArrayList<>());
    }

    public void setHotSwapPoint(HotSwapPoint hotSwapPoint) {
        context.putHotSwapPoint(hotSwapPoint);
    }

    public Boolean isExistHotSwapPoint(String className,String methodName) {
        return context.existsHotSwapPoint(className,methodName);
    }

    public HotSwapContext.InsertType matchType(String type){
        return HotSwapContext.InsertType.fromString(type);
    }

    public String rollBackHotSwapPoint(String className,String methodName) {

        //1.拿到当前类对应的源码字符串
        String currentCode = this.getProxyCodeByClassFullName(className);

        //2.找到前一个版本的热更新点
        List<HotSwapPoint> hotSwapPointList = context.getHotSwapPointsByMethodName(className, methodName);
        if(!hotSwapPointList.isEmpty()){
            HotSwapPoint hotSwapPoint = hotSwapPointList.get(hotSwapPointList.size() - 1);
            //3.构造对应的回滚代码
            String rollBack = context.generateProxyCode(currentCode, hotSwapPoint.getChangeRecord().getOldSourceCode());
            //4.热更新当前类
            context.notifyAgentBySocket(rollBack,hotSwapPoint.getClassName());
            //5.删除当前最后一个热更新点
            if(isExistHotSwapPoint(className,methodName)){
                context.removeLastHotSwapPoint(className,methodName);
                //6.更新内存中的源码字符串
                context.putClassProxyCode(className, rollBack);
            }
            return rollBack;
        }
        throw new HotSwapException("无法找到该热更新点历史记录");
    }

    public List<HotSwapPoint> getAllHotSwapPoints() {
        return context.getHotSwapPointRecord().values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void recoverHotSwapPoint(List<HotSwapPoint> hotSwapPoints) {

        Set<String> uniqueClassNames = hotSwapPoints.stream()
                .map(HotSwapPoint::getClassName)
                .collect(Collectors.toSet());

        uniqueClassNames.forEach(className -> {
            //1.当前类的所有最新热更新点
            Map<String, String> map = context.getClassMethodLatestHotSwapPoint(className);
            //2.获取当前类的源码字符串
            String sourceCode = this.getProxyCodeByClassFullName(className);
            String proxy = context.generateProxyCode(sourceCode, map);
            context.notifyAgentBySocket(proxy,className);
        });
    }
}