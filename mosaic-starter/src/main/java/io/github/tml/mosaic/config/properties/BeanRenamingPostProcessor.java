package io.github.tml.mosaic.config.properties;

import io.github.tml.mosaic.world.construct.MosaicWorld;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class BeanRenamingPostProcessor implements BeanPostProcessor, BeanDefinitionRegistryPostProcessor {
    // 这个方法是为了注册自定义的 BeanDefinitionRegistryPostProcessor
    @Resource
    private MosaicWorld mosaicWorld;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 不需要在这里做任何事
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 判断是否是 待替换bean名称的class
        List<Class<?>> replaceClasses = mosaicWorld.getReplaceClasses();

        for (Class<?> clazz : replaceClasses){
            if(clazz.isInstance(bean)){
                String newName = mosaicWorld.getWorldComponentManager().getComponentName(clazz);


            }
        }
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}