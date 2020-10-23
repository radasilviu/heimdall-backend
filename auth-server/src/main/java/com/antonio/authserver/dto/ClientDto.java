package com.antonio.authserver.dto;

import com.antonio.authserver.entity.Realm;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ClientDto {
    private String clientName;
    private String clientSecret;
    private Realm realm;


    public ClientDto() {
    }

    public ClientDto(String clientName, String clientSecret, Realm realm) {
        this.clientName = clientName;
        this.clientSecret = clientSecret;
        this.realm = realm;
    }
}
