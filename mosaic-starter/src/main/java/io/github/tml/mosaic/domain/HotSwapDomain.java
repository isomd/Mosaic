package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import io.github.tml.mosaic.util.ChunkHotSwapUtil;
import io.github.tml.mosaic.util.CubeTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        String code = getProxyCode(dto.getFullName());
        String proxy = ChunkHotSwapUtil.modify(code,
                dto.getTargetLine(),
                dto.getType(),
                dto::getProxyCode,
                Set.of(CubeTemplateUtil.getCubeImportPath()));
        context.getClassProxyCode().put(dto.getFullName(), proxy);

        return proxy;
    }

    public String getProxyCode(String fullName) {
        if(context.getClassProxyCode().containsKey(fullName)) {
            return context.getClassProxyCode().get(fullName);
        }

        String code = ChunkHotSwapUtil.decompileClassFromClassName(fullName);
        context.getClassProxyCode().put(fullName,code);
        return code;
    }

}