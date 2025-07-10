package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.NamedThreadFactory;
import io.github.tml.mosaic.core.execption.ActuatorException;
import io.github.tml.mosaic.cube.external.AngelCube;
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.external.MosaicVoid;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class AngelCubeActuator extends AbstractCubeActuator{

    private final Map<String, AngelCubeWorker> angelCubeWorkerMap = new ConcurrentHashMap<>();

    private ExecutorService threadPool;

    public AngelCubeActuator() {
        threadPool = new ThreadPoolExecutor(0 ,Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory("angel-cube-thread")
        );
    }

    @Override
    public <T> T execute(ExecuteContext executeContext) {
        MosaicCube cube = executeContext.getCube();
        if (!(cube instanceof AngelCube)) {
            throw new ActuatorException("{} is not an AngelCube.");
        }
        String cubeId = cube.cubeId();
        synchronized (cubeId){
            AngelCubeWorker worker = angelCubeWorkerMap.getOrDefault(cubeId, new AngelCubeWorker((AngelCube) cube));
            if (!angelCubeWorkerMap.containsKey(cubeId)) {
                worker.start();
                angelCubeWorkerMap.put(cubeId, worker);
            }else{
                worker.stop();
            }
        }
        return null;
    }

    public class AngelCubeWorker{

        private AngelCube angelCube;

        private final String name;

        private Future<?> startFuture;

        private static final String workerNamePrefix = "angel-cube-worker-";

        public AngelCubeWorker(AngelCube angelCube) {
            this.angelCube = angelCube;
            this.name = workerNamePrefix+angelCube.cubeId();
        }

        public void start() {
            startFuture = threadPool.submit(()->{
                log.info("Starting AngelCubeWorker: {}", name);
                angelCube.start();
            });
        }

        public void stop(){
            Future<?> stopFuture = threadPool.submit(() -> {
                log.info("Stopping AngelCubeWorker: {}", name);
                angelCube.stop();
                startFuture.cancel(true);
            });
        }

    }

}
