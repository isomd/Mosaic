package io.github.tml.mosaic.world.component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WorldComponent {

    private Class<?> clazz;

    private String componentClassName;

    public WorldComponent(Class<?> clazz, String componentClassName) {
        this.clazz = clazz;
        this.componentClassName = componentClassName;
    }
}