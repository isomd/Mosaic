package io.github.tml.mosaic.cube.factory.definition;

import lombok.Data;
@Data
public class CubeListenerDefinition {

    private String cubeId;
    private String name;
    private String className;
    private Class<?> clazz;
}