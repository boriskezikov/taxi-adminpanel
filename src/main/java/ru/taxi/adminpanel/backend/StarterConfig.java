package ru.taxi.adminpanel.backend;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StarterConfig {

    @Bean
    public WebServerFactoryCustomizer containerCustomizer() {
        return (container -> {
            container.set("/nemswiftsvc");
            container.setPort(Integer.valueOf(System.getenv("PORT")));
        });
    }

}
