package io.github.tml.mosaic.world.container;

import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.UniqueEntity;
import io.github.tml.mosaic.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class WorldContainer extends UniqueEntity {
    protected String name;

    protected List<Class<?>> componentClasses;

    private Map<Class<?>, String> worldComponentNames;

    public WorldContainer(String name, List<Class<?>> componentClasses) {
        super(CommonComponent.GuidAllocator().nextGUID());
        this.name = name;
        this.componentClasses = componentClasses;
        this.worldComponentNames = componentClasses.stream()
                .collect(HashMap::new, (map, clazz) -> map.put(clazz, StringUtil.getFirstLowerCase(clazz.getSimpleName()) + this.getId().toString()), HashMap::putAll);
    }

    public WorldContainer(GUID guid, String name, List<Class<?>> componentClasses) {
        super(guid);
        this.name = name;
        this.componentClasses = componentClasses;
        this.worldComponentNames = componentClasses.stream()
                .collect(HashMap::new, (map, clazz) -> map.put(clazz, StringUtil.getFirstLowerCase(clazz.getSimpleName()) + this.getId().toString()), HashMap::putAll);
    }

    public String getComponentName(Class<?> clazz) {
        return worldComponentNames.get(clazz);
    }

    public WorldContainer clone(){
        return new WorldContainer(this.getName() + "的副本", this.componentClasses);
    }
}