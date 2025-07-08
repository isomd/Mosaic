package io.github.tml.mosaic.core.world;

import io.github.tml.mosaic.config.mosaic.MosaicComponentConfig;
import io.github.tml.mosaic.util.BeanUtils;
import io.github.tml.mosaic.world.component.ComponentReplacer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ComponentBeanReplacer implements ComponentReplacer {

    @Resource
    private ApplicationContext applicationContext;

    private final List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();

    @Override
    // 属性替换
    public void replaceComponent(Map<Class<?>, String> newComponentNames, Map<Class<?>, String> oldComponentNames) {
        for (Class<?> clazz : componentClasses){
            String newComponentName = newComponentNames.get(clazz), oldComponentName = oldComponentNames.get(clazz);
            Object mainComponent = getComponent(clazz);
            Object oldComponent = getComponent(clazz, oldComponentName);
            Object newComponent = getComponent(clazz, newComponentName);

            BeanUtils.copyProperties(mainComponent, oldComponent);
            BeanUtils.copyProperties(newComponent, mainComponent);
            log.info("Component {} replace to {} success", clazz.getSimpleName(), newComponentName);
        }
    }

    @Override
    public void replaceComponent(Map<Class<?>, String> newComponentNames) {
        for (Class<?> clazz : componentClasses){
            String componentName = newComponentNames.get(clazz);
            Object mainComponent = getComponent(clazz);
            Object newComponent = getComponent(clazz, componentName);
            BeanUtils.copyProperties(newComponent, mainComponent);
            log.info("Component {} replace to {} success", clazz.getSimpleName(), componentName);
        }
    }

    protected <T> T getComponent(Class<T> clazz, String componentName){
        return applicationContext.getBean(componentName, clazz);
    }

    protected <T> T getComponent(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }
}
