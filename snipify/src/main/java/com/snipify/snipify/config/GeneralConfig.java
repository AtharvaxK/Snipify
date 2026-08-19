package com.snipify.snipify.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GeneralConfig {

    @Bean
    public DatabaseReader databaseReader()throws IOException {
        ClassPathResource resource=new ClassPathResource("GeoLite2-City.mmdb");
        try (InputStream inputStream=resource.getInputStream()){
            return new DatabaseReader.Builder(inputStream).build();
        }
    }
}
