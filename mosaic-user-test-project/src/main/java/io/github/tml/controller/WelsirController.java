package io.github.tml.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author welsir
 * @description :
 * @date 2025/6/23
 */
@RestController
@RequestMapping("/welsir")
public class WelsirController {

    @GetMapping("v1")
    public String welsir() {

        String welsir = "Welsir";
        System.out.println(welsir);
        return welsir;
    }

}