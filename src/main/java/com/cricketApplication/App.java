package com.cricketApplication;

import com.cricketApplication.dao.entities.GameDao;
import com.cricketApplication.dao.repositories.GameRepository;
import com.cricketApplication.service.interfaces.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {

        SpringApplication.run(App.class, args);

    }
}