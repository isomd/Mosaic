package io.github.tml.mosaic.cube.external;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MExtensionPackage {

    /**
     * 扩展包名称
     */
    String name() default "";
    
    /**
     * 扩展包描述
     */
    String description() default "";

    /**
     * 关联的Cube ID
     */
    String cubeId();
}