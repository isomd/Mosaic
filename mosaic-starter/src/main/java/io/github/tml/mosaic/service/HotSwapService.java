package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.entity.req.HotSwapPointRequest;
import io.github.tml.mosaic.entity.resp.CreateHotSwapPointResp;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;

import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
public interface HotSwapService {

    String getClassStrByClassFullName(String classFullName);

    String proxyCode(HotSwapDTO dto);

    CreateHotSwapPointResp createHotSwapPoint(HotSwapPointRequest dto);

    List<HotSwapPoint> getHotSwapPoints(String classFullName);

    String rollBackClassHotSwapPoint(String className, String methodName);
}