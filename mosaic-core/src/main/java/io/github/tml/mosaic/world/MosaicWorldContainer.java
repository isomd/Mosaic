package io.github.tml.mosaic.world;

import io.github.tml.mosaic.core.tools.copy.DeepCopyUtil;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.core.tools.guid.WUUID;
import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class MosaicWorldContainer extends WorldContainer implements Serializable {

    public MosaicWorldContainer(String name, Integer version, SlotManager slotManager, CubeContext cubeContext) {
        super(new GUUID(name + "-" + UUID.randomUUID()),version , name, slotManager, cubeContext);
    }

    @Override
    public WorldContainer clone() {
        MosaicWorldContainer clone = DeepCopyUtil.deepCopy(this);
        if(clone != null && clone != this){
            return clone;
        }

        throw new RuntimeException("clone failed");
    }
}
