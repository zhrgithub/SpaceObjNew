package com.spaceobj.spaceobj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.spaceobj.spaceobj.mapper") // 指定 Mapper 接口所在的包
@SpringBootApplication
public class SpaceObjApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaceObjApplication.class, args);
    }

}
