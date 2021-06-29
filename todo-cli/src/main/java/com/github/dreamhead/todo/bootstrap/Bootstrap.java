package com.github.dreamhead.todo.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({
        "com.github.dreamhead.zero"
})
@EnableJpaRepositories({
        "com.github.dreamhead.todo"
})
@EntityScan({
        "com.github.dreamhead.todo"
})
public class Bootstrap {
    public static void main(final String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}