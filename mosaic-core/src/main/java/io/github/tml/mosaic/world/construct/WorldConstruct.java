package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.world.WorldContainer;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public abstract class WorldConstruct {
    private WorldContainer runningWorldContainer;

    public WorldConstruct(WorldContainer runningWorldContainer) {
        this.runningWorldContainer = runningWorldContainer;
    }

    public void traverse(WorldContainer worldContainer){
        this.saveWorld();

        this.setRunningWorldContainer(worldContainer);

        this.constructWorld();
    }

    public abstract void constructWorld();

    public abstract void saveWorld();
}
