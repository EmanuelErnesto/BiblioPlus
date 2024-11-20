package com.emanuel.BiblioPlus.modules.users.infra.controllers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping()
    public String getPingMessage(){
        return "Cron have been called in " + LocalDateTime.now();
    }

    @Scheduled(cron = "0 */6 * * * *")
    private void pingServerEvery6Hours(){
        String message =  getPingMessage();

        System.out.println(message);
    }

}
