package io.github.tml.mosaic.install.domian.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Cube 相关的Listener 信息
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CubeListenerInfo {

    private String cubeId;
    private String name;
    private String className;
    private Class<?> clazz;
}