package io.synlabs.synvision;


import io.synlabs.synvision.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class SynVisionApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SynVisionApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SynVisionApplication.class);
    }
}

