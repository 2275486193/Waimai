package com.sky.aspect;

import com.github.pagehelper.Constant;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

/**
 * 自定义切面
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中对公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("公共字段自动填充");

        //获取当前方法数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        
        //获取当前被拦截的方法参数
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        //获取当前被拦截的方法参数中的实体对象
        Object entity = args[0];

        //准备赋值数据
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime currentTime = LocalDateTime.now();

        //根据不同操作类型，为对应属性通过反射赋值
        if(operationType == OperationType.INSERT){
            //为4个字段赋值
            try {
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class).invoke(entity,currentTime);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class).invoke(entity,currentId);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class).invoke(entity,currentTime);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class).invoke(entity,currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //为2个字段赋值
            try {
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class).invoke(entity,currentTime);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class).invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
