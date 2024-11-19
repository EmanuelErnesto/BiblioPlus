package com.emanuel.BiblioPlus.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.sql.DataSource;

@Configuration
public class SpringDatasourceConfig {

    @Autowired
    private Dotenv dotenv;

    @Bean
    public DataSource dataSource(){
        return DataSourceBuilder.create()
                .username(dotenv.get("DB_POSTGRES_USER"))
                .password(dotenv.get("DB_POSTGRES_PASSWORD"))
                .url(dotenv.get("DB_URL"))
                .build();
    }

}
