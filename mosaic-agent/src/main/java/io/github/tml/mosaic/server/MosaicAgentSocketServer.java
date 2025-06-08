package io.github.tml.mosaic.server;

import com.alibaba.fastjson2.JSON;
import io.github.tml.mosaic.MosaicChunkAgent;
import io.github.tml.mosaic.entity.DTO.AgentServerRequestDTO;
import io.github.tml.mosaic.util.JavaStringToFileUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author welsir
 * @description :
 * @date 2025/6/7
 */
public class MosaicAgentSocketServer {

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                int length = dis.readInt();
                byte[] jsonBytes = new byte[length];
                dis.readFully(jsonBytes);

                String json = new String(jsonBytes, StandardCharsets.UTF_8);
                AgentServerRequestDTO req = JSON.parseObject(json, AgentServerRequestDTO.class);
                Class<?> targetClass = Class.forName(req.getClassName());

                //内存中 编译class
                byte[] replace = JavaStringToFileUtil.compile(req.getClassName(), req.getClassCode());

                //热替换
                MosaicChunkAgent.getInstrumentation()
                        .redefineClasses(new ClassDefinition(targetClass,replace));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Class<?> findLoadedClass(String className) {
        for (Class<?> clazz : MosaicChunkAgent.getInstrumentation().getAllLoadedClasses()) {
            if (clazz.getName().equals(className)) {
                return clazz;
            }
        }
        return null;
    }
}