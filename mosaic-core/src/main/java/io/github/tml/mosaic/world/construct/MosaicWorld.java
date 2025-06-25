package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.world.component.WorldComponentManager;
import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import io.github.tml.mosaic.world.manager.WorldContainerManager;
import io.github.tml.mosaic.world.replace.ComponentReplace;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class MosaicWorld {
    protected WorldContainer runningWorldContainer;

    protected WorldContainerManager worldContainerManager;

    protected WorldComponentManager worldComponentManager;

    private List<ComponentReplace> replaceList;

    private List<Class<?>> replaceClasses;

    private final String ORIGINAL_WORLD_NAME = "original";

    public MosaicWorld(List<Class<?>> replaceClasses) {
        this.replaceClasses = replaceClasses;
        this.worldContainerManager = new WorldContainerManager();
        this.init();
    }

    public MosaicWorld(WorldContainer runningWorldContainer, List<Class<?>> classes) {
        this.replaceClasses = classes;
        this.runningWorldContainer = runningWorldContainer;
        this.worldContainerManager = new WorldContainerManager();
        this.init();
    }

    public void traverse(WorldContainer worldContainer){
        if(this.runningWorldContainer.equals(worldContainer)){
            return;
        }
        this.runningWorldContainer = worldContainer;
        init();
        replaceList.forEach(componentReplace -> componentReplace.replace(worldComponentManager));
    }
    // 初始化原始世界
    protected void init(){
        if(Objects.isNull(this.runningWorldContainer)){
            this.runningWorldContainer = WorldContainerFactory.createWorldContainer(ORIGINAL_WORLD_NAME, this.replaceClasses, true);
        }
        this.worldContainerManager.addWorldContainer(this.runningWorldContainer);

        this.worldComponentManager = this.runningWorldContainer.getWorldComponentManager();

        this.hotReplace();
    }
    // 做批量热更新
    protected void hotReplace(){

    }
}
