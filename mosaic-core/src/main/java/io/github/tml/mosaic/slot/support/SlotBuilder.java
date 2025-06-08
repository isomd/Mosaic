package io.github.tml.mosaic.slot.support;

import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.slot.Slot;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;

import java.util.Objects;
import java.util.Optional;

/**
 * 槽构建器
 */
public class SlotBuilder {

    public static BuilderContext builder(SlotManager manager) {
        return new BuilderContext(manager);
    }

   static class BuilderContext{

        private GUID slotId;

        private Slot.SetupCubeInfo setupCubeInfo;

        private SlotManager manager;

        protected BuilderContext(SlotManager manager) {
            this.setupCubeInfo = new Slot.SetupCubeInfo();
            this.manager = manager;
        }

        public BuilderContext slotId(String slotId) {
            this.slotId = new DotNotationId(slotId);
            return this;
        }

        public BuilderContext cubeId(GUID cubeId) {
            this.setupCubeInfo.setCubeId(cubeId);
            return this;
        }

       public BuilderContext exPackageId(String exPackageId) {
           this.setupCubeInfo.setExPackageId(new DotNotationId(exPackageId));
           return this;
       }

        public BuilderContext exPointId(String exPointId) {
            this.setupCubeInfo.setExPointId(new DotNotationId(exPointId));
            return this;
        }


        public Optional<Slot> build() {
           Slot slot = null;
           if(Objects.nonNull(slotId)) {
               slot = new Slot((DotNotationId) slotId);
               slot.Setup(setupCubeInfo);
               manager.registerSlot(slot);
           }
           return Optional.ofNullable(slot);
        }
    }
}
