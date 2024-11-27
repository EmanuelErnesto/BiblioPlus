package com.emanuel.BiblioPlus.modules.users.infra.controllers;

import com.emanuel.BiblioPlus.modules.users.clients.BiblioPlusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/ping")
public class PingController {

    private final Logger logger = LoggerFactory.getLogger(PingController.class);

    @Autowired
    private BiblioPlusClient biblioPlusClient;

    @GetMapping()
    public String getPingMessage(){
        return "Cron have been called in " + LocalDateTime.now();
    }

    @Scheduled(cron = "0 */6 * * * *")
    private void pingServerEvery6Hours(){
        String message =  biblioPlusClient.sendServerPing();
        logger.info(message);
    }

}