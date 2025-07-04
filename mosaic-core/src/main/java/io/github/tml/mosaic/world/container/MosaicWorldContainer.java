package io.github.tml.mosaic.world.container;

import io.github.tml.mosaic.core.tools.copy.DeepCopyUtil;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.world.component.WorldComponent;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MosaicWorldContainer extends WorldContainer implements Serializable {

    public MosaicWorldContainer(String name, Integer version, List<WorldComponent> components) {
        super(version , name, components);
    }

    public MosaicWorldContainer(GUID guid, Integer version, String name, List<WorldComponent> components) {
        super(guid, version, name, components);
    }

    // TODO: 深拷贝uuid问题
}
