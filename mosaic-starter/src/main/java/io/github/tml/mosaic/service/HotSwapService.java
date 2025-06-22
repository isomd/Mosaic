package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.entity.req.HotSwapPointRequest;
import io.github.tml.mosaic.entity.resp.CreateHotSwapPointResp;
import io.github.tml.mosaic.util.R;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
public interface HotSwapService {

    String getClassStrByClassFullName(String classFullName);

    String proxyCode(HotSwapDTO dto);

    CreateHotSwapPointResp createHotSwapPoint(HotSwapPointRequest dto);
}