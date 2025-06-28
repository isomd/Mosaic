package io.github.tml.mosaic.service;

import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;

import java.util.List;

public interface WorldService {
    WorldContainerVO createWorld(WorldContainerDTO worldContainerDTO);

    WorldContainerVO traverseWorld(String uuid);

    List<WorldContainerVO> getAllWorlds();

    WorldContainerVO removeWorld(String uuid);

    WorldContainerVO getNowWorld();
}