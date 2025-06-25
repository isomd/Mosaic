package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.convert.WorldContainerConvert;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.domain.WorldDomain;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;
import io.github.tml.mosaic.service.WorldService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorldServiceImpl implements WorldService {
    @Resource
    private WorldDomain worldDomain;

    @Override
    public WorldContainerVO createWorld(WorldContainerDTO worldContainerDTO) {
        return WorldContainerConvert.convert2VO(worldDomain.createWorld(worldContainerDTO));
    }

    @Override
    public WorldContainerVO traverseWorld(String uuid) {
        return WorldContainerConvert.convert2VO(worldDomain.traverseWorld(new GUUID(uuid)));
    }

    @Override
    public List<WorldContainerVO> getAllWorlds() {
        return worldDomain.getAllWorlds().stream()
                .map(WorldContainerConvert::convert2VO)
                .collect(Collectors.toList());
    }

    @Override
    public WorldContainerVO removeWorld(String uuid) {
        return WorldContainerConvert.convert2VO(worldDomain.removeWorld(new GUUID(uuid)));
    }

    @Override
    public WorldContainerVO getNowWorld() {
        return WorldContainerConvert.convert2VO(worldDomain.getNowWorld());
    }
}
