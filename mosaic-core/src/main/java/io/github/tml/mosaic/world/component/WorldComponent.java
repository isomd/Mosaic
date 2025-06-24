package io.github.tml.mosaic.world.component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WorldComponent {
    private String name;

    private Object component;

    public WorldComponent(String name, Object component) {
        this.name = name;
        this.component = component;
    }
}