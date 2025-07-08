package io.github.tml.mosaic.hotSwap.init;

import com.alibaba.fastjson.JSON;
import io.github.tml.mosaic.hotSwap.entity.resp.AgentServerResp;
import io.github.tml.mosaic.hotSwap.entity.req.AgentServerReq;

import java.io.*;
import java.net.Socket;

/**
 * @author welsir
 * @description :
 * @date 2025/7/2
 */
public class MosaicAgentSocketClient {

    private final String HOST = "localhost";

    private static MosaicAgentSocketClient INSTANCE;

    private int port;

    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static Socket socket;

    public static void register(int port) throws IOException {
        INSTANCE = new MosaicAgentSocketClient(port);
    }

    public static MosaicAgentSocketClient getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("MosaicAgentSocketClient 未初始化");
        }
        return INSTANCE;
    }

    private MosaicAgentSocketClient(int port) throws IOException {
        socket = new Socket(HOST, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public AgentServerResp pushMessage(String proxyCode,String className) {

        AgentServerReq req = new AgentServerReq();
        req.setClassName(className);
        req.setClassCode(proxyCode);

        String json = JSON.toJSONString(req);
        try {
            writer.write(json);
            writer.newLine();
            writer.flush();

            String response = reader.readLine();

            return JSON.parseObject(response, AgentServerResp.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}