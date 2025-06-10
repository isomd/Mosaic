package io.github.tml.mosaic.controller;

import com.alibaba.fastjson2.JSON;
import io.github.tml.mosaic.entity.DTO.HotSwapRequestDTO;
import io.github.tml.mosaic.entity.dto.AgentSocketRequestDTO;
import io.github.tml.mosaic.util.ASMUtil;
import io.github.tml.mosaic.util.CubeTemplateUtil;
import org.springframework.web.bind.annotation.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author welsir
 * @description :
 * @date 2025/6/8
 */
@RestController
@ResponseBody
@RequestMapping("/test/mosaic")
public class TestController {

    @PostMapping("v1")
    public void testHotSwap(@RequestBody HotSwapRequestDTO requestDTO){
        String proxy;
        if(requestDTO.getCmd()==1){
            proxy = ASMUtil.modify(requestDTO.getClassCode(), requestDTO.getLine(),
                    ASMUtil.CodeOp.REPLACE_ASSIGN_RIGHT,
                    () -> CubeTemplateUtil.generateCubeTemplateBySlotName(requestDTO.getSlotName())
                    , Set.of(CubeTemplateUtil.getCubeImportPath()));
        }else{
            proxy = ASMUtil.modify(requestDTO.getClassCode(), requestDTO.getLine(),
                    ASMUtil.CodeOp.INSERT_AFTER,
                    () -> CubeTemplateUtil.generateCubeTemplateByParams(requestDTO.getArgs())
                    , Set.of(CubeTemplateUtil.getCubeImportPath()));
        }
        AgentSocketRequestDTO send = new AgentSocketRequestDTO();
        send.setClassCode(proxy);
        send.setClassName(requestDTO.getClassName());
        String json = JSON.toJSONString(send);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        try (Socket socket = new Socket("localhost", 9999);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            dos.writeInt(jsonBytes.length);
            dos.write(jsonBytes);

            System.out.println("[Client] Class sent: " + requestDTO);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}