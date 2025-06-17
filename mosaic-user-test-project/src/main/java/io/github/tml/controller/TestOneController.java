package io.github.tml.controller;

import io.github.tml.mosaic.install.collector.LocalProjectClassCollector;
import io.github.tml.mosaic.install.domian.InfoContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test1")
public class TestOneController {

    @GetMapping("/context1")
    public void context() {
        LocalProjectClassCollector localProjectClassCollector = new LocalProjectClassCollector();
        InfoContext infoContext = new InfoContext();
        localProjectClassCollector.collect(infoContext);
    }
}
