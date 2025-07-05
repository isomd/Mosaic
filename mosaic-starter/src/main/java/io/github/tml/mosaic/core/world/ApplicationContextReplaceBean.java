package io.github.tml.mosaic.core.world;

import io.github.tml.mosaic.config.MosaicComponentConfig;
import io.github.tml.mosaic.util.BeanUtils;
import io.github.tml.mosaic.world.replace.ReplaceBeanContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApplicationContextReplaceBean implements ReplaceBeanContext {

    @Resource
    private ApplicationContext applicationContext;

    private final List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();

    @Override
    // 属性替换
    public void replaceBean(Map<Class<?>, String> componentNames) {
        for (Class<?> clazz : componentClasses){
            Object oldBean = applicationContext.getBean(clazz);
            Object newBean = getNewComponent(clazz, componentNames);
            BeanUtils.copyProperties(newBean, oldBean);
        }
    }

    protected <T> T getNewComponent(Class<T> clazz, Map<Class<?>, String> componentNames){
        String beanName = componentNames.get(clazz);
        return applicationContext.getBean(beanName, clazz);
    }
}
