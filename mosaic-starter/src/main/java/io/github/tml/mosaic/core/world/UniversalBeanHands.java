package io.github.tml.mosaic.core.world;

import io.github.tml.mosaic.config.MosaicComponentConfig;
import io.github.tml.mosaic.core.tools.copy.DeepCopyUtil;
import io.github.tml.mosaic.core.world.config.DynamicBeanNameModifier;
import io.github.tml.mosaic.world.container.WorldContainer;
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
            log.info("世界组件{}注册：{}", clazz.getName(), newContainer.getComponentName(clazz));
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
        Object o = applicationContext.getBean(beanName, beanClass);
        log.info("registerBean: {} : {}", beanName, o);
    }

    protected void registerBean(String newBeanName, String oldBeanName, Class<?> beanClass) {
        Object oldBean = applicationContext.getBean(oldBeanName, beanClass);
        Object newBean = DeepCopyUtil.deepCopy(oldBean);
        if(newBean == null){
            log.error("组件快照创建失败");
            return;
        }
        // 注册进容器
        ((ConfigurableApplicationContext) applicationContext)
                .getBeanFactory()
                .registerSingleton(newBeanName, newBean);
    }
}