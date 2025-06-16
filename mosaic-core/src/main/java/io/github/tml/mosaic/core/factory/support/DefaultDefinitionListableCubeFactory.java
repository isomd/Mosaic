package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.event.CubeEventBroadcaster;
import io.github.tml.mosaic.core.event.DefaultCubeEventBroadcaster;
import io.github.tml.mosaic.core.event.event.CubeDefinitionRegisteredEvent;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述: Cube工厂的核心实现类
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public class DefaultDefinitionListableCubeFactory extends ListableCubeFactory {

    // 使用ConcurrentHashMap保证线程安全
    private final Map<GUID, CubeDefinition> cubeDefinitionMap = new ConcurrentHashMap<>();
    private final CubeEventBroadcaster eventBroadcaster;

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

    // ==================== CubeDefinitionAccessor实现 ====================

    @Override
    public List<CubeDefinition> getAllCubeDefinitions() {
        return List.copyOf(cubeDefinitionMap.values());
    }

    @Override
    public Map<GUID, CubeDefinition> getAllCubeDefinitionMap() {
        return Map.copyOf(cubeDefinitionMap);
    }

    @Override
    public boolean containsCubeDefinition(String cubeId) {
        return cubeId != null && cubeDefinitionMap.containsKey(new GUUID(cubeId));
    }
}