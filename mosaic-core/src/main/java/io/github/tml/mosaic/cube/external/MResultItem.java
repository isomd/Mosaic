package io.github.tml.mosaic.cube.external;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MResultItem {

    // 参数名称
    String[] name();

    // 参数类型
    Class<?>[] type();

    // 参数描述
    String[] description() default "";
}
