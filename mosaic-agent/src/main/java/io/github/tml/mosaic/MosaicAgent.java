package io.github.tml.mosaic;

import io.github.tml.mosaic.core.watcher.MosaicAgentWatcher;
import io.github.tml.mosaic.server.MosaicAgentSocketServer;
import io.github.tml.mosaic.util.AgentUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @author welsir
 * @description :
 * @date 2025/5/27
 */

public class MosaicAgent {

    public static void agentmain(String args, Instrumentation inst) {

        AgentUtil.instrumentation = inst;
        AgentUtil.instrumentation.addTransformer(new MosaicAgentWatcher(), true);
        int port = Integer.parseInt(args.trim());
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket = serverSocket.accept();
            MosaicAgentSocketServer server = new MosaicAgentSocketServer(socket);
            new Thread(() -> {
                try {
                    server.start();
                } finally {
                    server.close();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}