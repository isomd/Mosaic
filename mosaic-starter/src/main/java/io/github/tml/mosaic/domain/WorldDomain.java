package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.config.MosaicInitConfig;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.world.construct.MosaicWorld;
import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class WorldDomain {
    @Resource
    private MosaicWorld mosaicWorld;

    public WorldContainer createWorld(WorldContainerDTO worldDTO){
        WorldContainer worldContainer = WorldContainerFactory.createWorldContainer(worldDTO.getName(), MosaicInitConfig.replaceClasses(), false);

        mosaicWorld.getWorldContainerManager().addWorldContainer(worldContainer);

        return worldContainer;
    }

    public WorldContainer traverseWorld(GUID guid){
        WorldContainer worldContainer = mosaicWorld.getWorldContainerManager().getWorldContainer(guid);

        mosaicWorld.traverse(worldContainer);

        return worldContainer;
    }

    public WorldContainer createQuickCopyWorld(GUID guid){
        WorldContainer worldContainer = mosaicWorld.getWorldContainerManager().getWorldContainer(guid);

        WorldContainer newWorldContainer = WorldContainerFactory.createWorldContainer(worldContainer);

        mosaicWorld.getWorldContainerManager().addWorldContainer(newWorldContainer);

        return newWorldContainer;
    }

    public List<WorldContainer> getAllWorlds(){
        return mosaicWorld.getWorldContainerManager().getAllWorldContainer();
    }

    public WorldContainer removeWorld(GUID guid){
        return mosaicWorld.getWorldContainerManager().removeWorldContainer(guid);
    }

    public WorldContainer getNowWorld(){
        return mosaicWorld.getRunningWorldContainer();
    }
}
