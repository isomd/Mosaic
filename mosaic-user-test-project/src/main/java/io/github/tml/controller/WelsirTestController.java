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

        String params = "";
        System.out.println(params);
        return "welsir";
    }

}