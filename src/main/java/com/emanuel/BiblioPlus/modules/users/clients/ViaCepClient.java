package com.emanuel.BiblioPlus.modules.users.clients;

import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viacep", url = "viacep.com.br/ws")
public interface ViaCepClient {

    @GetMapping("{cep}/json")
    AddressDTO getAddress(@PathVariable("cep") String cep);
}
