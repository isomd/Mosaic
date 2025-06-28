package io.github.tml.mosaic.core.world.config;

import io.github.tml.mosaic.config.MosaicComponentConfig;
import io.github.tml.mosaic.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DynamicBeanNameModifier implements BeanDefinitionRegistryPostProcessor {

    public Map<Class<?>, BeanDefinition> map = new HashMap<>();

    // 初始改名
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();
        for (Class<?> clazz : componentClasses){
            BeanDefinition beanDefinition = registry.getBeanDefinition(StringUtil.getFirstLowerCase(clazz.getSimpleName()));

            String newBeanName = MosaicComponentConfig.getBeanName(clazz);

            log.info("Modifying bean name from [{}] to [{}]", clazz.getName(), newBeanName);

            registry.removeBeanDefinition(StringUtil.getFirstLowerCase(clazz.getSimpleName()));

            beanDefinition.setPrimary(true);

            registry.registerBeanDefinition(newBeanName, beanDefinition);
            // 保存现有的BeanDefinition，以便后续创建新的Bean实例
            map.put(clazz, beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public BeanDefinition getBeanDefinition(Class<?> clazz) {
        if(map.containsKey(clazz)){
            BeanDefinition beanDefinition = map.get(clazz);
            beanDefinition.setPrimary(false);
            return beanDefinition;
        }
        throw new RuntimeException("beanDefinition not found");
    }
}