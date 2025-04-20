package com.spaceobj.spaceobj.beanCircleRefrence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

// 循环依赖测试控制器
@Controller
@RequestMapping("circleController")
public class CircleBeanController {

    // 注入 BeanA，用于测试循环依赖
    @Autowired
    private BeanA beanA;

    // 获取 BeanB 实例的接口
    @GetMapping("getBeanB")
    @ResponseBody
    public Map<String, Object> getBeanB() {
        Map<String, Object> resultMap = new HashMap<>();
        // 通过 BeanA 获取 BeanB 实例
        resultMap.put("beanB", beanA.getBeanB());

        return resultMap;
    }
}
