package io.github.tml.mosaic.entity.vo.world;

import io.github.tml.mosaic.core.tools.guid.GUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorldContainerVO{
    private GUID id;

    private String name;

    public WorldContainerVO(GUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "WorldContainerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
