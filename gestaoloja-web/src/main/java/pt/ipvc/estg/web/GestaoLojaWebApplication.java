package pt.ipvc.estg.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"pt.ipvc.estg"})
@EntityScan(basePackages = {"pt.ipvc.estg.model"})
@EnableJpaRepositories(basePackages = {"pt.ipvc.estg.web.repository"})
public class GestaoLojaWebApplication {

    public static void main(String[] args) {
        // Arranca a aplicação Spring Boot
        SpringApplication.run(GestaoLojaWebApplication.class, args);
        System.out.println("http://localhost:8080");
    }
}