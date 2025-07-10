package io.github.tml.mosaic.entity.vo.cube;

import lombok.Getter;

@Getter
    public enum CubeStatus {
        ACTIVE("运行中"),
        INACTIVE("未激活");

        private final String description;
        CubeStatus(String description) {
            this.description = description;
        }
    }