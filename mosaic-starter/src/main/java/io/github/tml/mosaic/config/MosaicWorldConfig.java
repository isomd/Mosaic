package io.github.tml.mosaic.config;

import io.github.tml.mosaic.world.construct.DefaultWorldConstruct;
import io.github.tml.mosaic.world.construct.WorldConstruct;
import io.github.tml.mosaic.world.manager.WorldContainerManager;
import io.github.tml.mosaic.world.replace.ComponentReplace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MosaicWorldConfig {
    // 留着扩展启动默认世界
    @Value("${mosaic.world.default-world:}")
    private String defaultWord;


    @Bean
    public WorldConstruct worldConstruct(ApplicationContext applicationContext) {
        WorldConstruct worldConstruct;
        List<ComponentReplace> replaceList = new ArrayList<>(applicationContext.getBeansOfType(ComponentReplace.class).values());
        worldConstruct = new DefaultWorldConstruct(replaceList);
        worldConstruct.init();
        return worldConstruct;
    }

    @Bean
    public WorldContainerManager worldContainerManager() {
        WorldContainerManager worldContainerManager = new WorldContainerManager();
        worldContainerManager.init();
        return worldContainerManager;
    }
}
