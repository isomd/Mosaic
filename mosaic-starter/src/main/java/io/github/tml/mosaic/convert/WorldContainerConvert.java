package io.github.tml.mosaic.convert;

import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;
import io.github.tml.mosaic.world.container.WorldContainer;

public class WorldContainerConvert {
    public static WorldContainerVO convert2VO(WorldContainer worldContainer){
        return worldContainer != null ? new WorldContainerVO(worldContainer.getId(), worldContainer.getName(), worldContainer.getVersion().get()) : new WorldContainerVO(null, "当前世界不存在", null);
    }

}
