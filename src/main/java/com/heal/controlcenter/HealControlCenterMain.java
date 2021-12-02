package com.heal.controlcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.heal.controlcenter")
@PropertySource(value = "classpath:conf.properties")
public class HealControlCenterMain {

    public static void main(String[] args) {
        SpringApplication.run(HealControlCenterMain.class, args);
    }
}
