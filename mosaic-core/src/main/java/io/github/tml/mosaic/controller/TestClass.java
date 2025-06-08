package io.github.tml.mosaic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ResponseBody
@RequestMapping("/mosaic")
@RestController
public class TestClass {

    @GetMapping("/test")
    public void foo() {
        int a = 1;
        int b = 2;
        int c = a + b;
        String out = "welsir";
        System.out.println(out);
    }

    /**
     * 123
     */
    public void bar() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i); // 行号约在 13
        }
    }
}