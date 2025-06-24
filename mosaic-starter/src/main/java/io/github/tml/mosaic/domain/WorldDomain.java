package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.convert.WorldContainerConvert;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.world.construct.MosaicWorld;
import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import io.github.tml.mosaic.world.manager.WorldContainerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class WorldDomain {
    @Resource
    private MosaicWorld mosaicWorld;

    @Resource
    private WorldContainerManager worldContainerManager;

    public WorldContainer createWorld(WorldContainerDTO worldDTO){
        WorldContainer worldContainer = WorldContainerFactory.createWorldContainer(worldDTO.getName(), mosaicWorld.getInfoContextInstaller());

        worldContainerManager.addWorldContainer(worldContainer);

        return worldContainer;
    }

    public WorldContainer traverseWorld(GUID guid){
        WorldContainer worldContainer = worldContainerManager.getWorldContainer(guid);

        mosaicWorld.traverse(worldContainer);

        return worldContainer;
    }

    public WorldContainer createQuickCopyWorld(GUID guid){
        WorldContainer worldContainer = worldContainerManager.getWorldContainer(guid);

        WorldContainer newWorldContainer = WorldContainerFactory.createWorldContainer(worldContainer);

        worldContainerManager.addWorldContainer(newWorldContainer);

        return newWorldContainer;
    }

    public List<WorldContainer> getAllWorlds(){
        return worldContainerManager.getAllWorldContainer();
    }

    public WorldContainer removeWorld(GUID guid){
        return worldContainerManager.removeWorldContainer(guid);
    }
}
