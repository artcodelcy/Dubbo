package com.toov.api.member.service.impl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@EnableDubbo
@SpringBootApplication
public class App {
  public static void main(String[] args) {
	SpringApplication.run(App.class, args);
}
}
