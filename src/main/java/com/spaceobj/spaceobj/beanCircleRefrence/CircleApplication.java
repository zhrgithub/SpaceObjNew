package com.spaceobj.spaceobj.beanCircleRefrence;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.spaceobj.spaceobj.beanCircleRefrence")
public class CircleApplication {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CircleApplication.class);
        BeanA beanA = context.getBean(BeanA.class);

        System.out.println(beanA.getBeanB());

    }
}
