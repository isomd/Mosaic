package io.github.tml.mosaic.world.container;

import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.core.tools.guid.UniqueEntity;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.world.component.WorldComponent;
import io.github.tml.mosaic.world.component.WorldComponentManager;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
public abstract class WorldContainer extends UniqueEntity {
    protected AtomicInteger version;

    protected String name;

    protected List<WorldComponent> components;

    protected WorldComponentManager worldComponentManager;

    public WorldContainer(Integer version, String name, List<WorldComponent> components) {
        super(CommonComponent.GuidAllocator().nextGUID());
        this.version = new AtomicInteger(version);
        this.name = name;
        this.components = components;
        this.worldComponentManager = new WorldComponentManager(components, this.getId().toString());
    }

    public void increaseVersion() {
        version.incrementAndGet();
    }

    public abstract WorldContainer clone();
}