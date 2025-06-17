package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.execption.ActuatorException;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.ExtPointResult;
import io.github.tml.mosaic.cube.ExtensionPackage;
import io.github.tml.mosaic.cube.ExtensionPoint;
import io.github.tml.mosaic.slot.Slot;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方块执行器代理
 */
@Slf4j
public class CubeActuatorProxy {

    private Map<Class<? extends CubeActuator>, CubeActuator> actuatorMap = new ConcurrentHashMap<>();

    protected CubeContext context;

    protected SlotManager slotManager;

    public void init(CubeContext context, SlotManager slotManager) {
        this.context = context;
        this.slotManager = slotManager;
        initActuators();
    }

    public void initActuators() {
        actuatorMap.put(GenericCubeActuator.class, new GenericCubeActuator());
        actuatorMap.put(AsyncCubeActuator.class, new AsyncCubeActuator());
    }

    private CubeActuator actuator(Class<? extends CubeActuator> actuatorClass) {
        return actuatorMap.get(actuatorClass);
    }

    public <T> T execute(GUID slotId, Object... args) throws ActuatorException {
        try {
            AbstractCubeActuator.ExecuteContext executeContext = getExecuteContext(slotId, args);
            Object execute = chooseActuator(executeContext)
                    .execute(executeContext);
            log.info("[TEST] execute result:{}", execute);
            return execute != null ? (T) execute : null;
        }catch (ActuatorException e){
            log.error("execute error, slot:{} error:{}", slotId, e.getMessage());
        }
        return null;
    }

    private AbstractCubeActuator.ExecuteContext getExecuteContext(GUID slotId, Object[] args) throws ActuatorException{
        Slot slot = Optional.ofNullable(slotManager.getSlot(slotId))
                .orElseThrow(()->new ActuatorException("actuator execute slot is null"));

        Slot.SetupCubeInfo setupCubeInfo = Optional.ofNullable(slot.getSetupCubeInfo())
                .orElseThrow(()-> new ActuatorException(String.format("slot %s not setup cube", slotId)));

        GUID cubeId = setupCubeInfo.getCubeId();
        GUID exPackageId = setupCubeInfo.getExPackageId();
        GUID exPointId = setupCubeInfo.getExPointId();

        Cube cube = Optional.ofNullable(context.getCube(cubeId))
                .orElseThrow(()->new ActuatorException(String.format("cube not found :%s", executeInfoLog(cubeId, null, null))));

        ExtensionPackage exPackage = Optional.ofNullable(cube.findExPackage(exPackageId))
                .orElseThrow(() -> new ActuatorException(String.format("exPackage not found : %s"
                        , executeInfoLog(cubeId, exPackageId, null))));

        ExtensionPoint exPoint = Optional.ofNullable(exPackage.findExPoint(exPointId))
                .orElseThrow(()-> new ActuatorException(String.format("exPoint not found : %s"
                        , executeInfoLog(cubeId, exPackageId, exPointId))));

        ExtPointResult returnResult = exPoint.getReturnResult();
        String resName = slot.getSetupCubeInfo().getResName();
        if (!returnResult.containsResultItem(resName)) {
            throw new ActuatorException(String.format("resName %s not found : %s", resName, executeInfoLog(cubeId, exPackageId, exPointId)));
        }

        return new CubeActuator.ExecuteContext(slot, cube.getMosaicCube(), exPackage.getMosaicExtPackage(), exPoint, args);
    }

    //TODO 待丰富
    private CubeActuator chooseActuator(CubeActuator.ExecuteContext executeContext) {
        return actuator(GenericCubeActuator.class);
    }

    private String executeInfoLog(GUID cube, GUID exPackage, GUID exPoint) {
        StringBuilder sb = new StringBuilder();
        if(cube != null){
            sb.append("| cube:").append(cube);
        }
        if(exPackage != null){
            sb.append("| exPackage:").append(exPackage);
        }
        if(exPoint != null){
            sb.append("| exPoint:").append(exPoint);
        }
        return sb.toString();
    }
}
