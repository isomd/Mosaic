package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.execption.ActuatorException;
import io.github.tml.mosaic.cube.ExtensionPoint;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public abstract class AbstractCubeActuator implements CubeActuator{

    protected AbstractCubeActuator(){

    }

    protected <T> T execute0(ExecuteContext executeContext) throws ActuatorException{

        MosaicExtPackage<?> mosaicExtPackage = executeContext.getExPackage();
        ExtensionPoint exPoint = executeContext.getExPoint();

        try {
            Method method = mosaicExtPackage.getClass().getMethod(exPoint.getMethodName(), exPoint.getParameterTypes());
            Object invoke = method.invoke(mosaicExtPackage, executeContext.getArgs());
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
