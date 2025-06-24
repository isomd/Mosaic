package io.github.tml.controller;

import io.github.tml.mosaic.install.collector.LocalProjectClassCollector;
import io.github.tml.mosaic.install.domian.InfoContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/steve/test")
public class TestOneController {

    @GetMapping("/context")
    public void context() {
        LocalProjectClassCollector localProjectClassCollector = new LocalProjectClassCollector();
        InfoContext infoContext = new InfoContext();
        localProjectClassCollector.collect(infoContext);
    }

    @GetMapping("/inject")
    public String testInject() {
        Object object = new Object();
        log.info(object.toString());
        return object.toString();
    }

    @GetMapping("/context1")
    public void context1() {
        LocalProjectClassCollector localProjectClassCollector = new LocalProjectClassCollector();
        InfoContext infoContext = new InfoContext();
        localProjectClassCollector.collect(infoContext);
    }

    @GetMapping("/inject1")
    public String testInject1() {
        Object object = new Object();
        log.info(object.toString());
        return object.toString();
    }

    @GetMapping("/context2")
    public void context2() {
        LocalProjectClassCollector localProjectClassCollector = new LocalProjectClassCollector();
        InfoContext infoContext = new InfoContext();
        localProjectClassCollector.collect(infoContext);
    }

    @GetMapping("/inject2")
    public String testInject2() {
        Object object = new Object();
        log.info(object.toString());
        return object.toString();
    }

    @GetMapping("/context3")
    public void context3() {
        LocalProjectClassCollector localProjectClassCollector = new LocalProjectClassCollector();
        InfoContext infoContext = new InfoContext();
        localProjectClassCollector.collect(infoContext);
    }

    @GetMapping("/inject3")
    public String testInject3() {
        Object object = new Object();
        log.info(object.toString());
        return object.toString();
    }

    @GetMapping("/context4")
    public void context4() {
        LocalProjectClassCollector localProjectClassCollector = new LocalProjectClassCollector();
        InfoContext infoContext = new InfoContext();
        localProjectClassCollector.collect(infoContext);
    }

    @GetMapping("/inject4")
    public String testInject4() {
        Object object = new Object();
        log.info(object.toString());
        return object.toString();
    }

    @GetMapping("/context5")
    public void context5() {
        LocalProjectClassCollector localProjectClassCollector = new LocalProjectClassCollector();
        InfoContext infoContext = new InfoContext();
        localProjectClassCollector.collect(infoContext);
    }

    @GetMapping("/inject5")
    public String testInject5() {
        Object object = new Object();
        log.info(object.toString());
        return object.toString();
    }

    @GetMapping("/context6")
    public void context6() {
        LocalProjectClassCollector localProjectClassCollector = new LocalProjectClassCollector();
        InfoContext infoContext = new InfoContext();
        localProjectClassCollector.collect(infoContext);
    }

    @GetMapping("/inject6")
    public String testInject6() {
        Object object = new Object();
        log.info(object.toString());
        return object.toString();
    }
}
