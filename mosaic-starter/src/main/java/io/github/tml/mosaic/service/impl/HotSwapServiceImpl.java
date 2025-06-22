package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.domain.HotSwapDomain;
import io.github.tml.mosaic.entity.dto.HotSwapDTO;
import io.github.tml.mosaic.service.HotSwapService;
import io.github.tml.mosaic.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
@Service
public class HotSwapServiceImpl implements HotSwapService {

    @Autowired
    private HotSwapDomain domain;

    @Override
    public R<?> getClassStrByClassFullName(String classFullName) {
        return R.success(domain.getProxyCode(classFullName));
    }

    @Override
    public R<?> proxyCode(HotSwapDTO dto) {
        return R.success(domain.proxyCodeByFullName(dto));
    }


}