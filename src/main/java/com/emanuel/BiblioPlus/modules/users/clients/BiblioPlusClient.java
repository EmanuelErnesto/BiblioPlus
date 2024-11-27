package com.emanuel.BiblioPlus.modules.users.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "biblioPlusClient", url = "${biblioplus.url}")
public interface BiblioPlusClient {

    @GetMapping("/ping")
    String sendServerPing();

}