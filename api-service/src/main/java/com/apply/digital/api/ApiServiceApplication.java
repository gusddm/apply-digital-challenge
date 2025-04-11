package com.apply.digital.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.apply.digital.db.entities")
@EnableJpaRepositories("com.apply.digital.db.repositories")
public class ApiServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(ApiServiceApplication.class, args);
  }
}
