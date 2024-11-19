package com.emanuel.BiblioPlus.modules.users.domain.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String estado;
    private String uf;

    public AddressDTO(){

    }

    public AddressDTO(
            String cep, String logradouro, String complemento, String bairro,
            String estado, String uf){
        this.cep = cep;
        this.logradouro = logradouro;
        this.complemento = complemento;
        this.bairro = bairro;
        this.estado = estado;
        this.uf = uf;
    }
}
