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

    @GetMapping("/context1")
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
}
