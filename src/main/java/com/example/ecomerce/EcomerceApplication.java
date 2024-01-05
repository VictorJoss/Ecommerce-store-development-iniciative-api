package com.example.ecomerce;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collection;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Ecommerce store development iniciative api", version = "1.0",
        description = "Documentation Ecommerce store development iniciative API v1.0", termsOfService = "http://www.apache.org/licenses/LICENSE-2.0.html",
        contact = @Contact(name = "Victor J. Ramirez Garcia", url = "https://linkedin.com/in/victor-josue-ramirez-garcia-899bb81a6/", email = "victorjosueramirezgarcia@gmail.com"),
        license = @License(name = "MIT", url = "http://www.apache.org/licenses/LICENSE-2.0.html")))

public class EcomerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomerceApplication.class, args);
    }

}
