package io.github.tml.mosaic.domain;

import io.github.tml.mosaic.convert.SlotConvert;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.entity.dto.SlotDTO;
import io.github.tml.mosaic.entity.dto.SlotSetupDTO;
import io.github.tml.mosaic.slot.Slot;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.slot.support.SlotBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 槽领域
 */
@Service
@Slf4j
public class SlotDomain {

    @Resource
    private SlotManager slotManager;

    /**
     * 创建槽
     * @param slotId
     * @return
     */
    public Optional<SlotDTO> createSlot(String slotId) {
        Optional<Slot> slotOptional = SlotBuilder.builder(slotManager).slotId(slotId).build();
        return slotOptional.map(SlotConvert::convert2DTO);
    }

    /**
     * 安装槽
     * @param slotSetupDTO
     * @return
     */
    public boolean setupSlot(SlotSetupDTO slotSetupDTO){
        return slotManager.setup(new DotNotationId(slotSetupDTO.getSlotId()), slotSetupDTO);
    }

    /**
     * 卸载槽安装信息
     */
    public boolean unSetupSlot(String slotId){
        return slotManager.unSetup(new DotNotationId(slotId));
    }

    /**
     * 槽是否存在
     * @param slotId
     * @return
     */
    public boolean hasSlot(String slotId){
        return slotManager.getSlot(new DotNotationId(slotId)) != null;
    }

    /**
     * 删除槽
     * @param slotId
     * @return
     */
    public void removeSlot(String slotId){
        slotManager.removeSlot(new DotNotationId(slotId));
    }

    /**
     * 获取所有槽列表
     * @return
     */
    public List<SlotDTO> getSlotList(){
        return slotManager.getSlotList().stream().map(SlotConvert::convert2DTO)
                .sorted(Comparator.comparing(o -> o.getId().toString()))
                .collect(Collectors.toList());
    }

}
