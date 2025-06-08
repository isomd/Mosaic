package io.github.tml.mosaic;

import io.github.tml.mosaic.server.MosaicAgentSocketServer;

import java.lang.instrument.Instrumentation;


/**
 * @author welsir
 * @description :
 * @date 2025/5/27
 */

public class MosaicChunkAgent {

    private static Instrumentation instrumentation;
    public static void agentmain(String args, Instrumentation inst) {

        System.out.println("run java agent...");
        instrumentation = inst;
        new Thread(() -> {
            try {
                new MosaicAgentSocketServer().start(9999);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

}