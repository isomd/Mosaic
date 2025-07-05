package io.github.tml.mosaic.core.world;

import io.github.tml.mosaic.config.mosaic.MosaicComponentConfig;
import io.github.tml.mosaic.util.BeanUtils;
import io.github.tml.mosaic.world.component.WorldComponentManager;
import io.github.tml.mosaic.world.replace.ReplaceBeanContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ApplicationContextReplaceBean implements ReplaceBeanContext {

    @Resource
    private ApplicationContext applicationContext;

    private final List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();

    @Override
    // 属性替换
    public void replaceBean(WorldComponentManager worldComponentManager) {
        for (Class<?> clazz : componentClasses){
            Object oldBean = applicationContext.getBean(clazz);
            Object newBean = getNewComponent(clazz, worldComponentManager);
            BeanUtils.copyProperties(newBean, oldBean);
        }
    }

    protected <T> T getNewComponent(Class<T> clazz, WorldComponentManager worldComponentManager){
        String beanName = worldComponentManager.getComponentName(clazz);
        return applicationContext.getBean(beanName, clazz);
    }
}
