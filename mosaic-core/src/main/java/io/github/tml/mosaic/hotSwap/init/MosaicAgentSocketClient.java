package io.github.tml.mosaic.hotSwap.init;

import com.alibaba.fastjson.JSON;
import io.github.tml.mosaic.MosaicAgent;
import io.github.tml.mosaic.entity.resp.AgentServerResp;
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

    public static MosaicAgentSocketClient INSTANCE;

    BufferedReader reader;
    BufferedWriter writer;
    Socket socket;

    public static MosaicAgentSocketClient getInstance(int port) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new MosaicAgentSocketClient(port);
        }
        return INSTANCE;
    }

    public MosaicAgentSocketClient(int port) throws IOException {
        socket = new Socket(HOST, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
            reader.close();
            writer.close();
            return JSON.parseObject(response, AgentServerResp.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}