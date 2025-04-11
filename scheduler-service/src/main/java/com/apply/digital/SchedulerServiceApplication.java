package com.apply.digital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.apply.digital")
@EntityScan("com.apply.digital.db.entities")
@EnableJpaRepositories("com.apply.digital.db.repositories")
public class SchedulerServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(SchedulerServiceApplication.class, args);
  }
}
