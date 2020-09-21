package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    public Client toClientDao(ClientDto clientDto){
        Client client = new Client();
        client.setClientName(clientDto.getClientName());
        return client;
    }
    public ClientDto toClientDto(Client client){
        return new ClientDto(client.getClientName());
    }
    public List<ClientDto> toClientDtoList(List<Client> list){
        return list.stream().map(client -> new ClientDto(client.getClientName()))
                .collect(Collectors.toList());
    }
}
