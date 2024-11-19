package com.emanuel.BiblioPlus.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringEnvironmentVariablesConfig {

    @Bean
    public Dotenv dotenv(){
        return Dotenv
                .configure()
                .systemProperties()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }
}
