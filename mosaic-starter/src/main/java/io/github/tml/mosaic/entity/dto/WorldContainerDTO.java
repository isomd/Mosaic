package io.github.tml.mosaic.entity.dto;


public class WorldContainerDTO {
    private String name;

    public WorldContainerDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null || name.isBlank() ? "新世界" :name;

    }
}