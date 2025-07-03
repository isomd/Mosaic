package io.github.tml.mosaic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * mosaic框架页面配置
 */
@Configuration
public class MosaicWebMvcConfig implements WebMvcConfigurer {

    public static final String MOSAIC_FRONT_PATH = "/mosaic/view/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(MOSAIC_FRONT_PATH+"**")
                .addResourceLocations("classpath:/view/");

    }

}
