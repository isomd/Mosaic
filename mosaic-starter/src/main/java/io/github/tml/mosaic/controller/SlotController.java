package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.service.SlotService;
import io.github.tml.mosaic.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/mosaic/slot")
public class SlotController {

    @Autowired
    private SlotService slotService;

    /**
     * 获取所有Cube插件信息
     */
    @GetMapping("/list")
    public R<?> getAllCubes() {
        return slotService.getSlotList();
    }

    @PostMapping("/createOrSetup")
    public R<?> createOrSetupSlot(@RequestBody AppendSlotReq req) {
        return slotService.createOrSetupSlot(req);
    }

    @GetMapping("/unSetup")
    public R<?> unSetup(@RequestParam("slotId") String slotId) {
        return slotService.unSetupSlot(slotId);
    }

    @GetMapping("/delete")
    public R<?> deleteSlot(@RequestParam("slotId") String slotId) {
        return slotService.deleteSlot(slotId);
    }
}
