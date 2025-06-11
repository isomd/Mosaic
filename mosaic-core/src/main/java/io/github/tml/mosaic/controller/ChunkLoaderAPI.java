//package io.github.tml.mosaic.controller;
//
//
//import com.alibaba.fastjson.JSON;
//import io.github.tml.mosaic.entity.dto.AgentSocketRequestDTO;
//import io.github.tml.mosaic.entity.dto.EditClassRequestDTO;
//import io.github.tml.mosaic.entity.dto.OriginClassReplaceRequestDTO;
//import io.github.tml.mosaic.entity.dto.RefreshCubeRequestDTO;
//import io.github.tml.mosaic.util.ASMUtil;
//import io.github.tml.mosaic.util.ChunkFileUtil;
//import io.github.tml.mosaic.util.CubeTemplateUtil;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.lang.management.ManagementFactory;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
///**
// * @author welsir
// * @description :
// * @date 2025/5/29
// */
//@RestController
//@RequestMapping("/mosaic/chunk")
//public class ChunkLoaderAPI {
//
//    @PostMapping("/replace")
//    public String SetupCube(@RequestBody OriginClassReplaceRequestDTO requestDTO) {
//        try {
//            return ChunkFileUtil.compileAndWriteToFile(requestDTO.getClassName(),requestDTO.getClassCode());
//        }catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @PostMapping("/refresh")
//    public void refreshProject(@RequestBody RefreshCubeRequestDTO requestDTO){
//        String pid = getCurrentPid();
//        String agentAttacherJar = "\"D:\\code\\forme\\agent\\demo\\agent-core\\target\\agent-core-0.0.1-SNAPSHOT.jar\"";
//        String agentJarPath = "\"D:\\code\\forme\\agent\\demo\\target\\demo-0.0.1-SNAPSHOT.jar\"";
//        Map<String, String> map = new HashMap<>();
//        map.put("className", requestDTO.getClassName());
//        map.put("path", requestDTO.getClassPath());
//        String pathByBase64 = Base64.getEncoder().encodeToString(JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8));
//        try {
//            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
//            ProcessBuilder pb = new ProcessBuilder(
//                    javaBin,
//                    "-cp", agentAttacherJar,
//                    "com.agent.MainTest",
//                    pid,
//                    agentJarPath,
//                    pathByBase64
//            );
//
//            pb.inheritIO();
//            Process process = pb.start();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String getCurrentPid() {
//        String name = ManagementFactory.getRuntimeMXBean().getName(); // format: pid@hostname
//        return name.split("@")[0];
//    }
//
//    @PostMapping("/edit")
//    public void edit(@RequestBody EditClassRequestDTO requestDTO) {
//
//        //jar -> .class文件
//        //Cube.execute(xxx,xxx,xxx);
//        String code = "package com.agentscript.controller;\n" +
//                "\n" +
//                "public class TestClass {\n" +
//                "    public void foo() {\n" +
//                "        int a = 1;\n" +
//                "        int b = 2;\n" +
//                "        int c = a + b;\n" +
//                "        String out = \"welsir\";\n" +
//                "        System.out.println(out);\n" +
//                "    }\n" +
//                "\n" +
//                "    public void bar() {\n" +
//                "        for (int i = 0; i < 10; ++i) {\n" +
//                "            System.out.println(i);\n" +
//                "        }\n" +
//                "    }\n" +
//                "}";
//        String proxy;
//        if(requestDTO.getCmd()==1){
//            proxy = ASMUtil.modify(code, requestDTO.getLine(),
//                    ASMUtil.CodeOp.REPLACE_ASSIGN_RIGHT,
//                    () -> CubeTemplateUtil.generateCubeTemplateBySlotName(requestDTO.getSlotName())
//                    , Set.of(CubeTemplateUtil.getCubeImportPath()));
//        }else{
//            proxy = ASMUtil.modify(code, requestDTO.getLine(),
//                    ASMUtil.CodeOp.INSERT_AFTER,
//                    () -> CubeTemplateUtil.generateCubeTemplateByParams(requestDTO.getParams())
//                    , Set.of(CubeTemplateUtil.getCubeImportPath()));
//        }
//        AgentSocketRequestDTO send = new AgentSocketRequestDTO();
//
//        send.setClassCode(proxy);
//        send.setClassName(requestDTO.getClassName());
//        String json = JSON.toJSONString(send);
//        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
//        try (Socket socket = new Socket("localhost", 9999);
//             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
//            dos.writeInt(jsonBytes.length);
//            dos.write(jsonBytes);
//
//            System.out.println("[Client] Class sent: " + requestDTO);
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}