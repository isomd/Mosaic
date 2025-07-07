package io.github.tml.mosaic.core.world;

import io.github.tml.mosaic.config.mosaic.MosaicComponentConfig;
import io.github.tml.mosaic.core.world.config.DynamicBeanNameModifier;
import io.github.tml.mosaic.world.WorldContainer;
import io.github.tml.mosaic.world.component.ComponentCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class UniversalBeanHands implements ComponentCreator {

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private DynamicBeanNameModifier dynamicBeanNameModifier;

    @Resource
    private ConfigurableApplicationContext context;

    private final List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();

    @Override
    public void createComponents(WorldContainer worldContainer) {
        for (Class<?> clazz : componentClasses){
            registerBean(worldContainer.getComponentName(clazz), clazz);
            log.info("World Component {} registry: {}", clazz.getName(), worldContainer.getComponentName(clazz));
        }
    }

    @Override
    public void createComponents(WorldContainer oldWorldContainer, WorldContainer newWorldContainer) {
        for (Class<?> clazz : componentClasses){
            registerBean(newWorldContainer.getComponentName(clazz), oldWorldContainer.getName(), clazz);
        }
    }

    protected void registerBean(String beanName, Class<?> beanClass) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getBeanFactory();
        if (!registry.containsBeanDefinition(beanName)) {
            BeanDefinition beanDefinition = dynamicBeanNameModifier.getBeanDefinition(beanClass);
            beanDefinition.setPrimary(false);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    protected void registerBean(String newBeanName, String oldBeanName, Class<?> beanClass) {
        // 创建快照bean
    }
}