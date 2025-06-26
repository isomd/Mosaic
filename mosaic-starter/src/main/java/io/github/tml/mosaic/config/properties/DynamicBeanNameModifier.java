package io.github.tml.mosaic.config.properties;

import io.github.tml.mosaic.config.MosaicInitConfig;
import io.github.tml.mosaic.util.StringUtil;
import io.github.tml.mosaic.world.construct.MosaicWorld;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DynamicBeanNameModifier implements BeanDefinitionRegistryPostProcessor {
    private final List<Class<?>> replaceClasses = MosaicInitConfig.replaceClasses();

    private final Map<Class<?>,  String> beanNameMap = replaceClasses.stream()
            .collect(ConcurrentHashMap::new, (map, clazz) -> map.put(clazz, StringUtil.getFirstLowerCase(clazz.getSimpleName()) + WorldContainerFactory.getOriginalUid()), ConcurrentHashMap::putAll);;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (Class<?> clazz : replaceClasses){
            BeanDefinition beanDefinition = registry.getBeanDefinition(StringUtil.getFirstLowerCase(clazz.getSimpleName()));
            String newBeanName = beanNameMap.get(clazz);

            log.info("Modifying bean name from [{}] to [{}]", clazz.getName(), newBeanName);

            registry.removeBeanDefinition(StringUtil.getFirstLowerCase(clazz.getSimpleName()));

            beanDefinition.setPrimary(true);

            registry.registerBeanDefinition(newBeanName, beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}