package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.event.CubeEventBroadcaster;
import io.github.tml.mosaic.core.event.DefaultCubeEventBroadcaster;
import io.github.tml.mosaic.core.event.event.CubeDefinitionRegisteredEvent;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述: Cube工厂的核心实现类
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public class DefaultDefinitionListableCubeFactory extends ListableCubeFactory {

    private Map<GUID, CubeDefinition> cubeDefinitionMap = new HashMap<>();
    private CubeEventBroadcaster eventBroadcaster;

    public DefaultDefinitionListableCubeFactory() {
        this.eventBroadcaster = DefaultCubeEventBroadcaster.broadcaster();
    }

    @Override
    public void registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition) {
        if (cubeId == null) {
            throw new IllegalArgumentException("CubeId cannot be null");
        }
        if (cubeDefinition == null) {
            throw new IllegalArgumentException("CubeDefinition cannot be null");
        }

        if (cubeDefinitionMap.containsKey(cubeId)) {
            log.warn("CubeDefinition {} is already registered", cubeId);
            return;
        }

        // 注册CubeDefinition
        CubeDefinition previousDefinition = cubeDefinitionMap.put(cubeId, cubeDefinition);

        // 发布事件
        try {
            CubeDefinitionRegisteredEvent event = new CubeDefinitionRegisteredEvent(this, cubeId, cubeDefinition);
            eventBroadcaster.broadcastEvent(event);

            log.debug("Successfully registered CubeDefinition [{}] and published event", cubeId);
        } catch (Exception e) {
            log.error("Failed to publish CubeDefinitionRegisteredEvent for cubeId [{}]", cubeId, e);
        }

        if (previousDefinition != null) {
            log.info("Replaced existing CubeDefinition for cubeId [{}]", cubeId);
        }
    }

    @Override
    protected CubeDefinition getCubeDefinition(GUID cubeId) throws CubeException {
        CubeDefinition cubeDefinition = cubeDefinitionMap.get(cubeId);
        if(cubeDefinition == null){
            log.warn("CubeDefinition not found for cubeId: {}", cubeId);
        }
        return cubeDefinition;
    }

    @Override
    public void preInstantiateSingletons() throws CubeException {
        cubeDefinitionMap.keySet().forEach(this::getCube);
    }
}
