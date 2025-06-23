package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.entity.req.HotSwapPointRequest;
import io.github.tml.mosaic.hotSwap.HotSwapContext;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author welsir
 * @description :
 * @date 2025/6/23
 */
@Data
public class HotSwapPointDTO {

    private String className;
    private HotSwapContext.InsertType changeType;
    private int lineNumber;
    private String methodName;
    private String newSourceCode;
    private String oldSourceCode;


    public static HotSwapPointDTO convert(HotSwapPointRequest dto,String oldSourceCode,String newSourceCode,HotSwapContext.InsertType type) {

        HotSwapPointDTO hotSwapPointDTO = new HotSwapPointDTO();

        BeanUtils.copyProperties(dto, hotSwapPointDTO);

        hotSwapPointDTO.setChangeType(type);
        hotSwapPointDTO.setOldSourceCode(oldSourceCode);
        hotSwapPointDTO.setNewSourceCode(newSourceCode);
        return hotSwapPointDTO;
    }
}