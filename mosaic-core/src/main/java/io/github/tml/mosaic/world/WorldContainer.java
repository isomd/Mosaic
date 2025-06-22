package io.github.tml.mosaic.world;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.UniqueEntity;
import io.github.tml.mosaic.core.tools.guid.WUUID;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

@Setter
@Getter
public abstract class WorldContainer extends UniqueEntity {
    protected AtomicInteger version;

    protected String name;

    protected SlotManager slotManager;

    protected CubeContext cubeContext;

    public WorldContainer(GUID id,Integer version, String name, SlotManager slotManager, CubeContext cubeContext) {
        super(id);
        this.version = new AtomicInteger(version);
        this.name = name;
        this.slotManager = slotManager;
        this.cubeContext = cubeContext;
    }

    public void increaseVersion() {
        version.incrementAndGet();
    }

    public abstract WorldContainer clone();
}