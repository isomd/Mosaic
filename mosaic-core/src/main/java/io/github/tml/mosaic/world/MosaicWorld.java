package io.github.tml.mosaic.world;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import io.github.tml.mosaic.world.manager.WorldContainerManager;
import io.github.tml.mosaic.world.component.ComponentCreator;
import io.github.tml.mosaic.world.component.ComponentReplacer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MosaicWorld {
    @Getter
    protected WorldContainer runningWorldContainer;
    @Getter
    private WorldContainer originalWorldContainer;

    protected WorldContainerManager worldContainerManager;

    private final ComponentReplacer componentReplacer;

    private final ComponentCreator componentCreator;

    protected List<Class<?>> componentsClasses;

    private final String ORIGINAL_WORLD_NAME = "original";

    public MosaicWorld(List<Class<?>> classes, ComponentReplacer componentReplacer, ComponentCreator componentCreator) {
        this.componentsClasses = classes;
        this.componentReplacer = componentReplacer;
        this.componentCreator = componentCreator;

        this.originalWorldContainer = createOriginalWorldContainer();
        this.runningWorldContainer = this.originalWorldContainer;
        this.worldContainerManager = new WorldContainerManager();
        this.worldContainerManager.put(this.originalWorldContainer);
        this.reload();
    }

    public MosaicWorld(WorldContainer runningWorldContainer, List<Class<?>> classes, ComponentReplacer componentReplacer, ComponentCreator componentCreator) {
        this.componentsClasses = classes;
        this.componentReplacer = componentReplacer;
        this.componentCreator = componentCreator;

        this.originalWorldContainer = createOriginalWorldContainer();
        this.runningWorldContainer = runningWorldContainer;
        this.worldContainerManager = new WorldContainerManager();
        this.worldContainerManager.put(this.originalWorldContainer);
        this.worldContainerManager.put(this.runningWorldContainer);
        this.reload();
    }

    // 重新加载世界的组件
    protected void reload(){
        componentReplacer.replaceComponent(this.runningWorldContainer.getWorldComponentNames());
    }
    // 切换世界
    public void traverse(WorldContainer worldContainer){
        if(this.runningWorldContainer.equals(worldContainer)){
            return;
        }
        this.runningWorldContainer = worldContainer;
        this.reload();
        log.info("世界切换：{}-{}", worldContainer.getName(), worldContainer.getId().toString());
    }

    public Boolean isRunningWorld(GUID guid){
        return this.runningWorldContainer.getId().equals(guid);
    }

    public WorldContainer createWorldContainer(String name){
        return createWorldContainer(name, false);
    }

    private WorldContainer createOriginalWorldContainer(){
        return createWorldContainer(ORIGINAL_WORLD_NAME, true);
    }

    private WorldContainer createWorldContainer(String name, Boolean isOriginal){
        WorldContainer worldContainer = WorldContainerFactory.createWorldContainer(name, this.componentsClasses, isOriginal);

        componentCreator.createComponents(worldContainer);

        return worldContainer;
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
}
