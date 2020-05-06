package com.dialogs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DialogsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DialogsApplication.class, args);
    }
}
