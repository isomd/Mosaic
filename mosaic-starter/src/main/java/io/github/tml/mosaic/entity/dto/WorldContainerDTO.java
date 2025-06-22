package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.world.MosaicWorldContainer;
import io.github.tml.mosaic.world.WorldContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorldContainerDTO {
    private GUID id;

    private String name;

    private Integer version;

    public WorldContainerDTO(GUID id, String name, Integer version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }
}
