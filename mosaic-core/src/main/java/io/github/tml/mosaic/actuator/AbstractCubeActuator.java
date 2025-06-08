package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.execption.ActuatorException;
import io.github.tml.mosaic.cube.ExtensionPackage;
import io.github.tml.mosaic.cube.ExtensionPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public abstract class AbstractCubeActuator implements CubeActuator{

    protected AbstractCubeActuator(){

    }

    protected <T> T execute0(ExecuteContext executeContext) throws ActuatorException{

        ExtensionPackage extensionPackage = executeContext.getExPackage();
        ExtensionPoint exPoint = executeContext.getExPoint();

        try {
            Method method = extensionPackage.getClass().getMethod(exPoint.getMethodName(), exPoint.getParameterTypes());
            Object invoke = method.invoke(extensionPackage, executeContext.getArgs());
            return (T) exPoint.getReturnType().cast(invoke);
        } catch (NoSuchMethodException e) {
            throw new ActuatorException("no such method error:" + e.getMessage());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ActuatorException("invoke method error:" + e.getMessage());
        } catch (Exception e){
            throw new ActuatorException("cube actuator execute point error:" + e.getMessage());
        }
    }



}
