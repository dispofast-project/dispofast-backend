package com.dispocol.dispofast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DispofastApplication {

	public static void main(String[] args) {
		SpringApplication.run(DispofastApplication.class, args);
	}

}
