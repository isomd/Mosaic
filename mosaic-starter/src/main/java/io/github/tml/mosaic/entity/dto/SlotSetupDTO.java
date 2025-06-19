package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.slot.Slot;
import lombok.Data;

@Data
public class SlotSetupDTO extends Slot.SetupCubeInfo {

    private String slotId;
}
