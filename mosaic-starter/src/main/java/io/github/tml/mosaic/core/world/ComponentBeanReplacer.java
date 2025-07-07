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
    public void replaceComponent(Map<Class<?>, String> componentNames) {
        for (Class<?> clazz : componentClasses){
            String componentName = componentNames.get(clazz);
            Object oldBean = applicationContext.getBean(clazz);
            Object newBean = getNewComponent(clazz, componentName);
            BeanUtils.copyProperties(newBean, oldBean);
            log.info("Component {} replace success", componentName);
        }
    }

    protected <T> T getNewComponent(Class<T> clazz, String componentName){
        return applicationContext.getBean(componentName, clazz);
    }
}
