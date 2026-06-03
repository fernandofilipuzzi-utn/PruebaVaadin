package com.utn.personas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Spring Boot + Vaadin.
 *
 * <p>{@code @SpringBootApplication} habilita la autoconfiguracion, el escaneo
 * de componentes (desde este paquete hacia abajo) y la configuracion embebida.</p>
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
