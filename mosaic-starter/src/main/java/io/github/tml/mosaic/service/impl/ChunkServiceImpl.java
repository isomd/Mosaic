package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.core.chunk.ChunkManager;
import io.github.tml.mosaic.service.ChunkService;
import io.github.tml.mosaic.util.R;
import org.springframework.stereotype.Service;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
@Service
public class ChunkServiceImpl implements ChunkService {
    @Override
    public R<?> getClassStrByClassFullName(String classFullName) {
        return R.success(ChunkManager.getProxyCode(classFullName));
    }
}