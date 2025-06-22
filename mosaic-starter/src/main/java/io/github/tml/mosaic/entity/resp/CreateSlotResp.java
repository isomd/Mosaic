package io.github.tml.mosaic.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSlotResp{

    private boolean isSuccess;

    private String errorMsg;
}
