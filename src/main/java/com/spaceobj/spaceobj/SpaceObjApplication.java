package com.spaceobj.spaceobj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 指定 MyBatis Mapper 接口所在的包路径
@MapperScan("com.spaceobj.spaceobj.mapper")
// 标记这是一个 Spring Boot 应用程序
@SpringBootApplication
public class SpaceObjApplication {

    // 应用程序入口方法
    public static void main(String[] args) throws ClassNotFoundException {
        // 启动 Spring Boot 应用
        SpringApplication.run(SpaceObjApplication.class, args);
//        Class<?> responseClass = Class.forName("org.apache.catalina.connector.Response");
//        System.out.println("Response class loaded from: " + responseClass.getProtectionDomain().getCodeSource().getLocation());
    }

}
