package com.bliznyuk.springbootsecurity;

import org.springframework.boot.SpringApplication;

public class TestSpringBootAndSpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringBootAndSpringSecurityApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
