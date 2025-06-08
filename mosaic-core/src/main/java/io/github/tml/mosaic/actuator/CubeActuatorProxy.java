package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.execption.ActuatorException;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.ExtensionPackage;
import io.github.tml.mosaic.cube.ExtensionPoint;
import io.github.tml.mosaic.slot.Slot;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方块执行器代理
 */
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
        AbstractCubeActuator.ExecuteContext executeContext = getExecuteContext(slotId, args);

        return chooseActuator(executeContext)
                .execute(executeContext);
    }

    private AbstractCubeActuator.ExecuteContext getExecuteContext(GUID slotId, Object[] args) {
        Slot slot = Optional.ofNullable(slotManager.getSlot(slotId))
                .orElseThrow(()->new ActuatorException("actuator execute slot is null"));

        Slot.SetupCubeInfo setupCubeInfo = Optional.ofNullable(slot.getSetupCubeInfo())
                .orElseThrow(()-> new ActuatorException(String.format("slot %s not setup cube", slot.getId())));

        Cube cube = Optional.ofNullable(context.getCube(setupCubeInfo.getCubeId()))
                .orElseThrow(()->new ActuatorException(String.format("cube %s is not found", setupCubeInfo.getCubeId())));

        ExtensionPackage exPackage = Optional.ofNullable(cube.findExPackage(setupCubeInfo.getExPackageId()))
                .orElseThrow(() -> new ActuatorException(String.format("cube %s exPackage %s is not found"
                        , setupCubeInfo.getCubeId(), setupCubeInfo.getExPackageId())));

        ExtensionPoint exPoint = Optional.ofNullable(exPackage.findExPoint(setupCubeInfo.getExPointId()))
                .orElseThrow(()-> new ActuatorException(String.format("cube %s exPackage %s exPoint %s is not found"
                        , setupCubeInfo.getCubeId(), setupCubeInfo.getExPackageId(), setupCubeInfo.getExPointId())));

        return new CubeActuator.ExecuteContext(slot, cube, exPackage, exPoint, args);
    }

    //TODO 待丰富
    private CubeActuator chooseActuator(CubeActuator.ExecuteContext executeContext) {
        return actuator(GenericCubeActuator.class);
    }

}
