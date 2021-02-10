package ru.taxi.adminpanel;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@PWA(
        name = "Taxi adminal console",
        shortName = "Adminpanel",
        offlineResources = {
                "./styles/offline.css",
                "./images/offline.png"})
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class AdminpanelStarter extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AdminpanelStarter.class, args);
    }

}