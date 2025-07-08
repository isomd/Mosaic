package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.domain.world.WorldDomain;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;
import io.github.tml.mosaic.service.WorldService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WorldServiceImpl implements WorldService {
    @Resource
    private WorldDomain worldDomain;

    @Override
    public WorldContainerVO createWorld(WorldContainerDTO worldContainerDTO) {
        return worldDomain.createWorld(worldContainerDTO);
    }

    @Override
    public WorldContainerVO traverseWorld(String uuid) {
        return worldDomain.traverseWorld(new GUUID(uuid));
    }

    @Override
    public List<WorldContainerVO> getAllWorlds() {
        return worldDomain.getAllWorlds();
    }

    @Override
    public WorldContainerVO removeWorld(String uuid) {
        return worldDomain.removeWorld(new GUUID(uuid));
    }

    @Override
    public WorldContainerVO getNowWorld() {
        return worldDomain.getNowWorld();
    }

    @Override
    public WorldContainerVO createArchive(String uuid) {
        return worldDomain.createQuickCopyWorld(new GUUID(uuid));
    }
}
