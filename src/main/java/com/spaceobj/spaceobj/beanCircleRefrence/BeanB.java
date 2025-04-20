package com.spaceobj.spaceobj.beanCircleRefrence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// BeanB 组件，用于演示循环依赖
@Component
public class BeanB {
    // 名称属性
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 持有 BeanA 的引用
    private final BeanA beanA;

    // 构造注入 BeanA
    @Autowired
    public BeanB(BeanA beanA) {
        this.beanA = beanA;
    }

    // 获取 BeanA 实例
    public BeanA getBeanA() {
        return beanA;
    }
}