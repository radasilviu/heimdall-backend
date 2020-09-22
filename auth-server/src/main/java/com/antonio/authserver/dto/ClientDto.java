package com.antonio.authserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDto {
    private String clientName;
    private String clientSecret;


    public ClientDto() {
    }

    public ClientDto(String clientName, String clientSecret) {
        this.clientName = clientName;
        this.clientSecret = clientSecret;
    }
}
