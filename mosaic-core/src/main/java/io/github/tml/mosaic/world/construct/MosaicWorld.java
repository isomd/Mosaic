package io.github.tml.mosaic.world.construct;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.world.container.WorldContainer;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import io.github.tml.mosaic.world.manager.WorldContainerManager;
import io.github.tml.mosaic.world.replace.ReplaceBeanContext;
import lombok.Getter;

import java.util.List;

public class MosaicWorld {
    @Getter
    protected WorldContainer runningWorldContainer;
    @Getter
    private WorldContainer originalWorldContainer;

    protected WorldContainerManager worldContainerManager;

    private final ReplaceBeanContext replaceBeanContext;

    protected List<Class<?>> componentsClasses;

    private final String ORIGINAL_WORLD_NAME = "original";

    public MosaicWorld(List<Class<?>> classes, ReplaceBeanContext replaceBeanContext) {
        this.replaceBeanContext = replaceBeanContext;
        this.componentsClasses = classes;
        this.originalWorldContainer = WorldContainerFactory.createWorldContainer(ORIGINAL_WORLD_NAME, classes, true);
        this.runningWorldContainer = this.originalWorldContainer;
        this.worldContainerManager = new WorldContainerManager();
        this.worldContainerManager.put(this.originalWorldContainer);
        this.reload();
    }

    public MosaicWorld(WorldContainer runningWorldContainer, List<Class<?>> classes, ReplaceBeanContext replaceBeanContext) {
        this.replaceBeanContext = replaceBeanContext;
        this.componentsClasses = classes;
        this.originalWorldContainer = WorldContainerFactory.createWorldContainer(ORIGINAL_WORLD_NAME, classes, true);
        this.runningWorldContainer = runningWorldContainer;
        this.worldContainerManager = new WorldContainerManager();
        this.worldContainerManager.put(this.originalWorldContainer);
        this.worldContainerManager.put(this.runningWorldContainer);
        this.reload();
    }

    // 重新加载世界的组件
    protected void reload(){
        replaceBeanContext.replaceBean(this.runningWorldContainer.getWorldComponentNames());

        this.hotReplace();
    }
    // 切换世界
    public void traverse(WorldContainer worldContainer){
        if(this.runningWorldContainer.equals(worldContainer)){
            return;
        }
        this.runningWorldContainer = worldContainer;
        this.reload();
    }

    public Boolean isRunningWorld(GUID guid){
        return this.runningWorldContainer.getId().equals(guid);
    }

    public WorldContainer createWorldContainer(String name){
        return WorldContainerFactory.createWorldContainer(name, componentsClasses, false);
    }

    public void registryWorldContainer(WorldContainer worldContainer){
        worldContainerManager.put(worldContainer);
    }

    public Boolean contains(GUID guid){
        return worldContainerManager.contains(guid);
    }

    public WorldContainer getWorldContainer(GUID guid){
        return worldContainerManager.getWorldContainer(guid);
    }

    public List<WorldContainer> getAllWorldContainer(){
        return worldContainerManager.getAllWorldContainer();
    }

    public WorldContainer removeWorldContainer(GUID guid){
        return worldContainerManager.removeWorldContainer(guid);
    }

    public Integer worldSize(){
        return worldContainerManager.worldSize();
    }

    // 做批量热更新
    protected void hotReplace(){

    }
}
