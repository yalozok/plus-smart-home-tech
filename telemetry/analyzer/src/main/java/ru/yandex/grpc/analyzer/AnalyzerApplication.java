package ru.yandex.grpc.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }

}
