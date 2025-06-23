package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.manager.WorldContainerManager;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class WorldConstruct {
    protected WorldContainer runningWorldContainer;

    public WorldConstruct() {
    }

    public WorldConstruct(WorldContainer runningWorldContainer) {
        this.runningWorldContainer = runningWorldContainer;
    }

    public void traverse(WorldContainer worldContainer){
        if (!this.saveWorld()){
            this.setRunningWorldContainer(worldContainer);

            this.constructWorld();
        }
        throw new RuntimeException("World traverse failed");
    }

    public void init(){
        this.runningWorldContainer = WorldContainerManager.getOriginalWorldContainer();

        this.constructWorld();
    }

    public abstract void constructWorld();

    public abstract Boolean saveWorld();
}
