package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"ru.practicum.ewm", "ru.practicum.statsclient"})
//@ComponentScan(basePackages = {"ru.practicum.ewm", "ru.practicum.statsclient"})
public class ExploreWithMeApp {
    public static void main(String[] args) {
        SpringApplication.run(ExploreWithMeApp.class, args);
    }
}
