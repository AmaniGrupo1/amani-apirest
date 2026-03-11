package com.amani.amaniapirest;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Log4j
public class AmaniApirestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmaniApirestApplication.class, args);
        log.info("Hola mundo");
    }
}
