package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.resp.CreateSlotResp;
import io.github.tml.mosaic.service.SlotService;
import io.github.tml.mosaic.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        return R.success(Map.of(
                "slotList", slotService.getSlotList()
        ));
    }

    @PostMapping("/createOrSetup")
    public R<?> createOrSetupSlot(@RequestBody AppendSlotReq req) {
        CreateSlotResp resp = slotService.createOrSetupSlot(req);
        if (resp.isSuccess()) {
            return R.success();
        }else{
            return R.error(resp.getErrorMsg());
        }
    }

    @GetMapping("/unSetup")
    public R<?> unSetup(@RequestParam("slotId") String slotId) {
        if (slotService.unSetupSlot(slotId)) {
            return R.success();
        }else{
            return R.error("unSetup slot failed");
        }
    }

    @GetMapping("/delete")
    public R<?> deleteSlot(@RequestParam("slotId") String slotId) {
        slotService.deleteSlot(slotId);
        return R.success();
    }
}
