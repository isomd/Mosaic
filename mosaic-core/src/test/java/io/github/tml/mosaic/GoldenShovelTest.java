package io.github.tml.mosaic;

import java.lang.reflect.Method;

public class GoldenShovelTest {

    public void testGoldenShovel() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testGoldenShovel");
        System.out.println(method.getReturnType().getName().equals("void"));
    }

    public static void main(String[] args) throws NoSuchMethodException {
        new GoldenShovelTest().testGoldenShovel();
    }

}
