package com.nicokuchling.wegfest.scene_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nicokuchling.wegfest")
public class SceneServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SceneServiceApplication.class, args);
    }

}
