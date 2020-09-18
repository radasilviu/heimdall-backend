package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;

import java.util.List;
import java.util.stream.Collectors;

public class ClientMapper {

    public Client toClientDao(ClientDto clientDto){
        Client client = new Client();
        client.setClientName(clientDto.getClientName());
        client.setClientSecret(clientDto.getClientSecret());
        return client;
    }
    public ClientDto toClientDto(Client client){
        return new ClientDto(client.getClientName(),client.getClientSecret());
    }
    public List<ClientDto> toClientDtoList(List<Client> list){
        return list.stream().map(client -> new ClientDto(client.getClientName(), client.getClientSecret()))
                .collect(Collectors.toList());
    }
}
