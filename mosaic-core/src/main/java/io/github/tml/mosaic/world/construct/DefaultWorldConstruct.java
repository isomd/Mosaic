package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.world.component.WorldComponentManager;
import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.manager.WorldContainerManager;
import io.github.tml.mosaic.world.replace.ComponentReplace;

import java.util.List;

public class DefaultWorldConstruct extends GenerateWorldConstruct{
    private List<ComponentReplace> replaceList;

    private WorldComponentManager worldComponentManager;

    public DefaultWorldConstruct(WorldContainer runningWorldContainer, List<ComponentReplace> replaceList) {
        super(runningWorldContainer);
        this.replaceList = replaceList;
        this.worldComponentManager = new WorldComponentManager();
        worldComponentManager.init(runningWorldContainer);
    }

    public DefaultWorldConstruct(List<ComponentReplace> replaceList) {
        this.replaceList = replaceList;
        this.worldComponentManager = new WorldComponentManager();
        worldComponentManager.init(WorldContainerManager.getOriginalWorldContainer());
    }

    @Override
    protected void replaceBeans() {
        for (ComponentReplace replace : replaceList){
            replace.replace(worldComponentManager);
        }
    }

    @Override
    protected void hotReplaceCode() {

    }
}
