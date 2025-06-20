package io.github.tml.mosaic.controller;

import com.alibaba.fastjson.JSON;
import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.config.MosaicChunkConfig;
import io.github.tml.mosaic.core.chunk.ChunkManager;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.entity.req.HotSwapRequestDTO;
import io.github.tml.mosaic.entity.req.AgentSocketRequestDTO;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.util.ChunkHotSwapUtil;
import io.github.tml.mosaic.util.CubeTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * @author welsir
 * @description :
 * @date 2025/6/8
 */
@Slf4j
@RestController
@ResponseBody
@RequestMapping("/test/mosaic")
public class TestController {

    @Autowired
    SlotManager slotManager;

    @Resource
    MosaicChunkConfig mosaicChunkConfig;


    @PostMapping("v1")
    public void testHotSwap(@RequestBody HotSwapRequestDTO requestDTO) {
        String proxy;

        String className = requestDTO.getClassName();
        String code = ChunkHotSwapUtil.decompileClassFromClassName(className);
        if(requestDTO.getCmd()==1){
            proxy = ChunkManager.proxyCodeByFullName(requestDTO.getClassName(),requestDTO.getLine(), ChunkManager.InsertType.REPLACE_ASSIGN_RIGHT,CubeTemplateUtil.generateCubeTemplateBySlotName(code));
        }else{
            String slotId = requestDTO.getSlotId();

            GoldenShovel.slotBootStrap().slotId(slotId)
                    .exPackageId(requestDTO.getExPackageId())
                    .exPointId(requestDTO.getExPointId())
                    .cubeId(new GUUID(requestDTO.getPluginId()))
                    .resName(requestDTO.getResName())
                    .build();

            proxy = ChunkManager.proxyCodeByFullName(
                    className,requestDTO.getLine(), ChunkManager.InsertType.INSERT_AFTER,
                    CubeTemplateUtil.buildCodeTemplate(slotId, requestDTO.getArgs()
            ));
        }
        AgentSocketRequestDTO send = new AgentSocketRequestDTO();
        send.setClassCode(proxy);
        send.setClassName(requestDTO.getClassName());
        String json = JSON.toJSONString(send);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        try (Socket socket = new Socket("localhost", mosaicChunkConfig.getPort());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            dos.writeInt(jsonBytes.length);
            dos.write(jsonBytes);

            log.info("[Client] Class sent: {}", requestDTO);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("classString/{className}")
    public String classString(@PathVariable String className){
        return ChunkManager.getProxyCode(className);
    }
}