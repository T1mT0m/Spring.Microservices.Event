package com.github.bwar.sb.ms.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ProdutServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProdutServiceApplication.class, args);
	}

}
