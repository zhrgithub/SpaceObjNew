package com.spaceobj.spaceobj.beanCircleRefrence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

// BeanA 组件，用于演示循环依赖
@Component
public class BeanA {

    // 年龄属性
    private String age;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    // 持有 BeanB 的引用
    private final BeanB beanB;

    // 使用 @Lazy 注解来解决循环依赖问题
    @Autowired
    @Lazy
    public BeanA(BeanB beanB) {
        this.beanB = beanB;
    }

    // 获取 BeanB 实例
    public BeanB getBeanB() {
        return beanB;
    }
}