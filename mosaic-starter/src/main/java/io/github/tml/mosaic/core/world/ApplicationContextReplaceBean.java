package io.github.tml.mosaic.core.world;

import io.github.tml.mosaic.config.MosaicComponentConfig;
import io.github.tml.mosaic.world.component.WorldComponentManager;
import io.github.tml.mosaic.world.replace.ComponentReplace;
import io.github.tml.mosaic.world.replace.ReplaceBeanContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ApplicationContextReplaceBean implements ReplaceBeanContext {

    @Resource
    private ApplicationContext applicationContext;

    private final List<Class<?>> componentClasses = MosaicComponentConfig.getComponentClasses();

    @Override
    // 属性替换
    public void replaceBean(WorldComponentManager worldComponentManager) {
        List<ComponentReplace> beansOfType = new ArrayList<>(applicationContext.getBeansOfType(ComponentReplace.class).values());
        // 遍历待替换的bean实例
        for (ComponentReplace bean : beansOfType){
            Class<? extends ComponentReplace> clazz = bean.getClass();
            Field[] fields = clazz.getDeclaredFields();
            // 循环判断当前属性是否为需替换的属性
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object o = field.get(bean);
                    if(!Objects.isNull(o)){
                        Class<?> fieldClass = o.getClass();
                        for (Class<?> componentClass : componentClasses){
                            // 需替换 则反射替换
                            if (componentClass.isAssignableFrom(fieldClass)){
                                field.set(bean, getNewComponent(componentClass, worldComponentManager));
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected <T> T getNewComponent(Class<T> clazz, WorldComponentManager worldComponentManager){
        String beanName = worldComponentManager.getComponentName(clazz);
        return applicationContext.getBean(beanName, clazz);
    }
}
