package com.coherentsolutions.springai.l5rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class L5RagApplication {
    public static void main(String[] args) {
        SpringApplication.run(L5RagApplication.class, args);
    }
}
