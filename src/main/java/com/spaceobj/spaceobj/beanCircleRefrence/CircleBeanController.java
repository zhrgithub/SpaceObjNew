package com.spaceobj.spaceobj.beanCircleRefrence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("circleController")
public class CircleBeanController {

    @Autowired
    private BeanA beanA;

    @GetMapping("getBeanB")
    @ResponseBody
    public Map<String, Object> getBeanB() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("beanB", beanA.getBeanB());

        return resultMap;
    }
}
