package com.reporte_ciudadano.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

	public static void main(String[] args) {
		// System.out.println("Hello word");
		SpringApplication.run(BackendApplication.class, args);
	}

}
