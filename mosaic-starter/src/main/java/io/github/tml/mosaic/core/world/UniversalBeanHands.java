package io.github.tml.mosaic.core.world;

import io.github.tml.mosaic.config.mosaic.MosaicComponentConfig;
import io.github.tml.mosaic.core.tools.copy.DeepCopyUtil;
import io.github.tml.mosaic.core.world.config.DynamicBeanNameModifier;
import io.github.tml.mosaic.world.WorldContainer;
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
public class UniversalBeanHands {

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private DynamicBeanNameModifier dynamicBeanNameModifier;

    @Resource
    private ConfigurableApplicationContext context;

    public void createBeans(WorldContainer newContainer) {
        List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();
        for (Class<?> clazz : componentClasses){
            registerBean(newContainer.getComponentName(clazz), clazz);
            log.info("World Component {} registry: {}", clazz.getName(), newContainer.getComponentName(clazz));
        }

    }

    public void createBeans(WorldContainer oldContainer, WorldContainer newContainer) {
        List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();
        for (Class<?> clazz : componentClasses){
            registerBean(newContainer.getComponentName(clazz), oldContainer.getName(), clazz);
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
        Object oldBean = applicationContext.getBean(oldBeanName, beanClass);
//        Object newBean = DeepCopyUtil.deepCopy(oldBean);
        Object newBean = null;
        if(newBean == null){
            log.error("Component create failed {}", beanClass.getName());
            return;
        }
        // 注册进容器
        ((ConfigurableApplicationContext) applicationContext)
                .getBeanFactory()
                .registerSingleton(newBeanName, newBean);
    }
}