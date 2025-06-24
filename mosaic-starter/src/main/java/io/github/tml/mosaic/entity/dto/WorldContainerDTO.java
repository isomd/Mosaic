package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.core.tools.guid.GUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorldContainerDTO {
    private String name;

    public WorldContainerDTO(String name) {
        this.name = name == null || name.isBlank() ? "新世界" :name;
    }
}