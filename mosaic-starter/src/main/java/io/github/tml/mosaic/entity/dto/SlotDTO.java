package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.slot.Slot;
import lombok.Data;

import java.util.Objects;
import java.util.stream.Stream;

@Data
public class SlotDTO extends Slot {

    public SlotDTO(String slotName) {
        super(slotName);
    }

    /**
     * 是否安装完毕，安装信息是否完备
     * @return
     */
    public boolean isSetupReady(){
        SetupCubeInfo setupCubeInfo = this.getSetupCubeInfo();
        if(Objects.isNull(setupCubeInfo)){
            return false;
        }
        return Stream.of(setupCubeInfo.getCubeId(), setupCubeInfo.getExPackageId(), setupCubeInfo.getExPointId()).allMatch(Objects::nonNull);
    }
}
