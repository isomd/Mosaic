package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
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

    protected InfoContextInstaller infoContextInstaller;

    protected WorldComponentManager worldComponentManager;

    private List<ComponentReplace> replaceList;

    private final String ORIGINAL_WORLD_NAME = "original";

    public MosaicWorld(List<ComponentReplace> list, InfoContextInstaller infoContextInstaller) {
        this.replaceList = list;
        this.infoContextInstaller = infoContextInstaller;
        this.init();
    }

    public MosaicWorld(WorldContainer runningWorldContainer, List<ComponentReplace> list, InfoContextInstaller infoContextInstaller) {
        this.runningWorldContainer = runningWorldContainer;
        this.infoContextInstaller = infoContextInstaller;
        this.replaceList = list;
        this.init();
    }

    public void traverse(WorldContainer worldContainer){
        this.runningWorldContainer = worldContainer;

        replaceList.forEach(componentReplace -> componentReplace.replace(worldComponentManager));
    }
    // 初始化原始世界
    protected void init(){
        if(Objects.isNull(this.runningWorldContainer)){
            this.runningWorldContainer = WorldContainerFactory.createWorldContainer(ORIGINAL_WORLD_NAME, this.infoContextInstaller);
        }
        this.worldContainerManager = new WorldContainerManager();
        this.worldContainerManager.addWorldContainer(this.runningWorldContainer);

        this.worldComponentManager = this.runningWorldContainer.getWorldComponentManager();

        this.hotReplace();
    }
    // 做批量热更新
    protected void hotReplace(){

    }
}
