package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.entity.req.HotSwapPointRequest;
import io.github.tml.mosaic.entity.resp.CreateHotSwapPointResp;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import io.github.tml.mosaic.util.R;

import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
public interface HotSwapService {

    R<?> getClassStrByClassFullName(String classFullName);

    R<?> proxyCode(HotSwapDTO dto);

    R<?> createHotSwapPoint(HotSwapPointRequest dto);

    R<?> getHotSwapPoints(String classFullName);

    R<?> rollBackClassHotSwapPoint(String className, String methodName);
}