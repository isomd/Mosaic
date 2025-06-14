package io.github.tml.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author welsir
 * @description :
 * @date 2025/6/9
 */
@RestController
@RequestMapping("/welsir")
public class WelsirTestController {

    @GetMapping("/v1")
    public String welsir() {
        String params = "welsir big stupid pig";
        String params2 = "twj big stupid fuck pig";
        String params3 = "Genius is good good";
        return "success";
    }

}