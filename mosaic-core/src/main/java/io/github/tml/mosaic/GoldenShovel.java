package io.github.tml.mosaic;

import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.slot.support.SlotBuilder;

/**
 * 金铲铲工具类
 * 为开发者，框架使用者提供的全局工具类，能方便便利的进行槽构建，方块执行。
 */
public class GoldenShovel {

    private static CubeActuatorProxy cubeActuatorProxy;

    private static SlotManager slotManager;

    public static void loadCubeActuatorProxy(CubeActuatorProxy proxy){
        cubeActuatorProxy = proxy;
    }

    public static void loadSlotManager(SlotManager manager){
        slotManager = manager;
    }

    /**
     *  方块执行构造指导
     * @return
     */
    public static ExecuteBootstrap executeBootStrap(){
        return new ExecuteBootstrap();
    }

    /**
     * 槽构建指导
     * @return
     */
    public static SlotBootStrap slotBootStrap(){
        return new SlotBootStrap();
    }

    protected static class ExecuteBootstrap{

        private String slotId;

        private Object[] args;

        private ExecuteBootstrap() {
        }

        public ExecuteBootstrap slotId(String slotId){
            this.slotId = slotId;
            return this;
        }

        public <T> T run(Object... args){
            this.args = args;
            return cubeActuatorProxy.execute(new DotNotationId(slotId), this.args);
        }
    }

    protected static class SlotBootStrap{

        private final SlotBuilder.BuilderContext builder = SlotBuilder.builder(slotManager);

        private String slotId;

        private SlotBootStrap(){

        }

        public SlotBootStrap slotId(String slotId) {
            builder.slotId(slotId);
            return this;
        }

        public SlotBootStrap cubeId(GUID cubeId) {
            builder.cubeId(cubeId);
            return this;
        }

        public SlotBootStrap exPackageId(String exPackageId) {
            builder.exPackageId(exPackageId);
            return this;
        }

        public SlotBootStrap exPointId(String exPointId) {
            builder.exPointId(exPointId);
            return this;
        }


        public SlotBootStrap build() {
            slotId = builder.build()
                    .map((slot) -> slot.getId().toString())
                    .orElse("");
            return this;
        }

        public ExecuteBootstrap executeBootStrap(){
            ExecuteBootstrap executeBootstrap = GoldenShovel.executeBootStrap();
            return executeBootstrap.slotId(slotId);
        }
    }
}
