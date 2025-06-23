package io.github.tml.mosaic.convert;

import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.world.container.WorldContainer;

public class WorldContainerConvert {
    public static WorldContainerDTO convert2DTO(WorldContainer worldContainer){
        return new WorldContainerDTO(worldContainer.getId(), worldContainer.getName(), worldContainer.getVersion().get());
    }
}
