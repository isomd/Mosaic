package io.github.tml.mosaic.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import io.github.tml.mosaic.core.components.DeployContextHolder;
import io.github.tml.mosaic.util.AgentUtil;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class MosaicAgentSocketServer {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public MosaicAgentSocketServer(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void start() {
        String responseJson = "";
        try {
            // 读取请求
            String json = reader.readLine();

            // 加载请求类 & 反序列化
            Class<?> req = AgentUtil.getClassByInst("io.github.tml.mosaic.entity.req.AgentServerRequestDTO");
            ClassLoader targetClassLoader = req.getClassLoader();
            Thread.currentThread().setContextClassLoader(targetClassLoader);

            ParserConfig parserConfig = new ParserConfig();
            parserConfig.setDefaultClassLoader(targetClassLoader);
            Object reqObj = JSON.parseObject(json, req, parserConfig, Feature.SupportAutoType);

            // 获取参数
            Method getClassNameMethod = req.getMethod("getClassName");
            String className = (String) getClassNameMethod.invoke(reqObj);
            Method getClassCodeMethod = req.getMethod("getClassCode");
            String classCode = (String) getClassCodeMethod.invoke(reqObj);

            DeployContextHolder.set(Map.of("code", classCode));

            // 热替换逻辑
            AgentUtil.instrumentation.retransformClasses(AgentUtil.getClassByInst(className));

            if ("true".equals(DeployContextHolder.get().get("flag"))) {
                responseJson = "{\"isSuccess\":true,\"message\":\"Class :" + className + " 更新成功\"}";
            } else {
                responseJson = "{\"isSuccess\":false,\"message\":\"更新失败：" + DeployContextHolder.get().get("errorMsg") + "\"}";
            }
            DeployContextHolder.clear();
            writer.write(responseJson);
            writer.newLine();
            writer.flush();

        } catch (InvocationTargetException | UnmodifiableClassException | NoSuchMethodException |
                 IllegalAccessException | IOException e) {
            responseJson = "{\"isSuccess\":false,\"message\":\"更新失败：" + e.getClass().getSimpleName() + " - " + e.getMessage() + "\"}";
            throw new RuntimeException(responseJson);
        }
    }

    public void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
