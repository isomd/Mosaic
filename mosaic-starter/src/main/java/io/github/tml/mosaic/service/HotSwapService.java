package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.util.R;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
public interface HotSwapService {

    R<?> getClassStrByClassFullName(String classFullName);

    R<?> proxyCode(HotSwapDTO dto);
}