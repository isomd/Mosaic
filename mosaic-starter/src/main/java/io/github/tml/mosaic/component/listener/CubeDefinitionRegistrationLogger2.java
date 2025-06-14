package io.github.tml.mosaic.component.listener;

import io.github.tml.mosaic.core.event.event.CubeDefinitionRegisteredEvent;
import io.github.tml.mosaic.core.event.listener.CubeDefinitionRegisteredEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CubeDefinition注册事件监听器示例
 */
@Component
@Slf4j
public class CubeDefinitionRegistrationLogger2 implements CubeDefinitionRegisteredEventListener {
    
    @Override
    public void onEvent(CubeDefinitionRegisteredEvent event) {
        log.info("【2】CubeDefinition registered - CubeId: [{}], ClassName: [{}], Scope: [{}]",
                event.getCubeId(), 
                event.getCubeDefinition().getClassName(),
                event.getCubeDefinition().getModel());
    }
    
    @Override
    public int getPriority() {
        return 2;
    }
    
    @Override
    public String getListenerName() {
        return "CubeDefinitionRegistrationLogger-----2";
    }
}