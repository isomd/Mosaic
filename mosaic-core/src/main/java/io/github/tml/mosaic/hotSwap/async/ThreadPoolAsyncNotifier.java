package io.github.tml.mosaic.hotSwap.async;

import io.github.tml.mosaic.core.components.DeployContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolAsyncNotifier implements AsyncNotifier {

    private final ExecutorService executor;

    public ThreadPoolAsyncNotifier() {
        this.executor = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "MosaicAgentWatcher-Notifier-" + count.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        });
    }

    @Override
    public void notifyAsync(DeployContext context) {
        executor.submit(() -> {
            try {
                // 这里写你的后置逻辑，例如日志打印、事件发布等
                System.out.println("异步通知：类 " + context.getClassName() + " 热部署完成");
                // 也可以调用你自己的监听器链
            } catch (Throwable t) {
                t.printStackTrace(); // 防止异常导致线程池线程死亡
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}