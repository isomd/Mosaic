package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.config.mosaic.MosaicComponentConfig;
import io.github.tml.mosaic.convert.WorldContainerConvert;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.world.UniversalBeanHands;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;
import io.github.tml.mosaic.world.construct.MosaicWorld;
import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorldDomain {
    @Resource
    private MosaicWorld mosaicWorld;

    @Resource
    private UniversalBeanHands universalBeanHands;

    public WorldContainerVO createWorld(WorldContainerDTO worldDTO){
        // 创建新世界容器
        WorldContainer worldContainer = mosaicWorld.createWorldContainer(worldDTO.getName());

        mosaicWorld.registryWorldContainer(worldContainer);
        // 创建新世界的组件并注册
        universalBeanHands.createBeans(worldContainer);

        return WorldContainerConvert.convert2VO(worldContainer);
    }

    public WorldContainerVO traverseWorld(GUID guid){
        // 当前切换的世界是否存在
        if(mosaicWorld.contains(guid)){
            if(!mosaicWorld.isRunningWorld(guid)){
                WorldContainer worldContainer = mosaicWorld.getWorldContainer(guid);

                mosaicWorld.traverse(worldContainer);

                return WorldContainerConvert.convert2VO(worldContainer);
            }
            return WorldContainerConvert.convert2VO(mosaicWorld.getRunningWorldContainer());
        }
        return null;
    }

    public WorldContainerVO createQuickCopyWorld(GUID guid){
        WorldContainer worldContainer = mosaicWorld.getWorldContainer(guid);

        WorldContainer newWorldContainer = WorldContainerFactory.createWorldContainer(worldContainer);

        mosaicWorld.registryWorldContainer(newWorldContainer);

        // 创建快照世界的组件并注册
        universalBeanHands.createBeans(worldContainer, newWorldContainer);

        return WorldContainerConvert.convert2VO(newWorldContainer);
    }

    public List<WorldContainerVO> getAllWorlds(){
        return mosaicWorld.getAllWorldContainer().stream().map(WorldContainerConvert::convert2VO).collect(Collectors.toList());
    }

    public WorldContainerVO removeWorld(GUID guid){
        WorldContainer worldContainer = mosaicWorld.getWorldContainer(guid);
        if(mosaicWorld.getOriginalWorldContainer().equals(worldContainer) || mosaicWorld.worldSize() <= 1){
            return null;
        }
        if (mosaicWorld.isRunningWorld(guid)){
            WorldContainer originalWorld = mosaicWorld.getOriginalWorldContainer();

            mosaicWorld.traverse(originalWorld);
        }
        return WorldContainerConvert.convert2VO(mosaicWorld.removeWorldContainer(guid));
    }

    public WorldContainerVO getNowWorld(){
        return WorldContainerConvert.convert2VO(mosaicWorld.getRunningWorldContainer());
    }
}
