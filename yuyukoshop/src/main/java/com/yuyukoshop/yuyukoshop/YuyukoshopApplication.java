package com.yuyukoshop.yuyukoshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yuyukoshop.yuyukoshop.mapper")
public class
YuyukoshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(YuyukoshopApplication.class, args);
	}

}
