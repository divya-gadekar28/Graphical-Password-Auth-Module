package com.GraphicalPasswordAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for launching the Graphical Password Authentication Spring Boot application.
 * 
 * This application uses icon-based (graphical) passwords for enhanced security and user interaction.
 */
@SpringBootApplication
public class GraphicalPasswordAuthApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command-line arguments passed during startup.
     */
    public static void main(String[] args) {
        SpringApplication.run(GraphicalPasswordAuthApplication.class, args);
    }

}
