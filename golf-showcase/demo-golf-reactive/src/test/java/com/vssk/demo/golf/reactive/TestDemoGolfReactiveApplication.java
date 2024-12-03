package com.vssk.demo.golf.reactive;

import org.springframework.boot.SpringApplication;

public class TestDemoGolfReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.from(DemoGolfReactiveApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
