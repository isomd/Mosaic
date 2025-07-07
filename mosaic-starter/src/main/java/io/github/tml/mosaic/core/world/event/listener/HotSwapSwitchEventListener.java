package io.github.tml.mosaic.core.world.event.listener;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.world.event.event.AfterWorldTransitionEvent;
import io.github.tml.mosaic.domain.WorldDomain;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author welsir
 * @description :
 * @date 2025/7/7
 */

@Component
public class HotSwapSwitchEventListener extends WorldEventListener<AfterWorldTransitionEvent>{

    @Resource
    private WorldDomain worldDomain;

    @Override
    public void onEvent(AfterWorldTransitionEvent event) {

        GUID oldWorldId = event.getOldWorldId();
        GUID curWorldId = event.getNewWorldId();
        HotSwapContext oldContext = worldDomain.getWorldComponent(oldWorldId, HotSwapContext.class);
        HotSwapContext curContext = worldDomain.getWorldComponent(curWorldId, HotSwapContext.class);

        Map<String, Map<String, List<HotSwapPoint>>> oldContextHotSwapPointRecord = oldContext.getHotSwapPointRecord();
        Map<String, Map<String, List<HotSwapPoint>>> curContextHotSwapPointRecord = curContext.getHotSwapPointRecord();

        //交集或当前世界存在&&旧世界不存在的热更新点 则直接update
        Set<String> intersection = new HashSet<>(oldContextHotSwapPointRecord.keySet());
        intersection.retainAll(curContextHotSwapPointRecord.keySet());
        Set<String> newExclusive = new HashSet<>(curContextHotSwapPointRecord.keySet());
        newExclusive.removeAll(oldContextHotSwapPointRecord.keySet());

        for (String className : intersection) {
            updateClassByClassName(className,curContext);
        }

        for (String className : newExclusive) {
            updateClassByClassName(className,curContext);
        }

        //rollback
        Set<String> oldExclusive = new HashSet<>(oldContextHotSwapPointRecord.keySet());
        oldExclusive.removeAll(curContextHotSwapPointRecord.keySet());

        for (String className : oldExclusive) {
            rollbackHotSwapPointToOrigin(className,oldContext);
        }
    }

    @Override
    public Class<AfterWorldTransitionEvent> getEventType() {
        return AfterWorldTransitionEvent.class;
    }

    private void updateClassByClassName(String className,HotSwapContext context){
        Map<String, String> map = context.getClassMethodLatestHotSwapPoint(className);

        String sourceCode = context.getProxyCode(className);
        String proxy = context.generateProxyCode(sourceCode, map);
        context.notifyAgentBySocket(proxy,className);
    }

    private void rollbackHotSwapPointToOrigin(String className,HotSwapContext context) {

        String currentClassCode = context.getProxyCode(className);

        Map<String, String> classMethodOriginalHotSwapPoint = context.getClassMethodOriginalHotSwapPoint(className);

        String update = context.generateProxyCode(currentClassCode, classMethodOriginalHotSwapPoint);
        context.notifyAgentBySocket(update,className);

    }
}