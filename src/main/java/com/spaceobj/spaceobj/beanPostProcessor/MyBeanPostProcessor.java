package com.spaceobj.spaceobj.beanPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

// Bean 后置处理器，用于在 Bean 实例化前后进行处理
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    // 在 Bean 初始化之前执行的方法
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 可以在这里对 bean 进行预处理
        // System.out.println("BeanPostProcessor postProcessBeforeInitialization: " + beanName);
        return bean;  // 返回可能被修改的 bean
    }

    // 在 Bean 初始化之后执行的方法
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 可以在这里对 bean 进行后处理 
        // System.out.println("BeanPostProcessor postProcessAfterInitialization: " + beanName);
        return bean;  // 返回可能被修改的 bean
    }
}
