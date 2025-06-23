package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.entity.dto.HotSwapPointDTO;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import io.github.tml.mosaic.hotSwap.model.ChangeRecord;
import io.github.tml.mosaic.hotSwap.model.DeployVersion;
import io.github.tml.mosaic.util.ChunkHotSwapUtil;
import io.github.tml.mosaic.util.CodeTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author welsir
 * @description :
 * @date 2025/6/22
 */
@Service
@Slf4j
public class HotSwapDomain {

    private final HotSwapContext context = new HotSwapContext();

    /**
     *  根据参数进行代码增强并返回源码字符串
     * @param dto
     * @return
     */
    public String proxyCodeByFullName(HotSwapDTO dto) {

        String code = getProxyCodeByClassFullName(dto.getClassName());
        String proxy = ChunkHotSwapUtil.modify(code,
                dto.getTargetLine(),
                dto.getType(),
                dto::getProxyCode,
                Set.of(CodeTemplateUtil.getCubeImportPath()));
        context.putClassProxyCode(dto.getClassName(), proxy);
        return proxy;
    }

    public String getProxyCodeByClassFullName(String fullName) {

        if(context.ProxyCodeContainsKey(fullName)) {
            return context.getProxyCode(fullName);
        }

        String code = ChunkHotSwapUtil.decompileClassFromClassName(fullName);
        context.putClassProxyCode(fullName,code);
        return code;
    }

    public DeployVersion generateHotSwapPoint(HotSwapPointDTO pointDTO) {

        ChangeRecord record = new ChangeRecord(pointDTO.getClassName(),pointDTO.getMethodName(),pointDTO.getChangeType(),null);

        DeployVersion deployVersion = new DeployVersion(new GUUID(""), LocalDateTime.now(),record);

        return deployVersion;

    }

}