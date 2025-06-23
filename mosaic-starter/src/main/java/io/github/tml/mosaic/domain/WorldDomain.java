package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.convert.WorldContainerConvert;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.construct.WorldConstruct;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import io.github.tml.mosaic.world.manager.WorldContainerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class WorldDomain {
    @Resource
    private WorldConstruct worldConstruct;

    @Resource
    private WorldContainerManager worldContainerManager;

    public WorldContainerDTO createWorld(WorldContainerDTO worldDTO){
        WorldContainer worldContainer = WorldContainerFactory.createWorldContainer(worldDTO.getName());

        worldContainerManager.addWorldContainer(worldContainer);

        return WorldContainerConvert.convert2DTO(worldContainer);
    }

    public WorldContainerDTO traverseWorld(GUID guid){
        WorldContainer worldContainer = worldContainerManager.getWorldContainer(guid);

        worldConstruct.traverse(worldContainer);

        return WorldContainerConvert.convert2DTO(worldContainer);
    }

    public WorldContainerDTO createQuickCopyWorld(GUID guid){
        WorldContainer worldContainer = worldContainerManager.getWorldContainer(guid);

        WorldContainer newWorldContainer = WorldContainerFactory.createWorldContainer(worldContainer);

        worldContainerManager.addWorldContainer(newWorldContainer);

        return WorldContainerConvert.convert2DTO(worldContainer);
    }

    public void removeWorld(GUID guid){
        worldContainerManager.removeWorldContainer(guid);
    }
}
