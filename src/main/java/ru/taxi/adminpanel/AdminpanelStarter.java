package ru.taxi.adminpanel;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@PWA(
        name = "Taxi admin console",
        shortName = "Adminpanel",
        offlineResources = {
                "./styles/offline.css",
                "./images/offline.png"})
@Push
@EnableCaching
@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class AdminpanelStarter extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AdminpanelStarter.class, args);
    }

}
