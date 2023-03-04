package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.practicum.client.StatsClient;

@SpringBootApplication
@Import(StatsClient.class)
public class EvmServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EvmServiceApp.class, args);
    }
}