package com.piedpiper.goodhabits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class GoodHabitsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GoodHabitsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GoodHabitsApplication.class);
    }

}
