package com.antonio.authserver.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(source = "clientName", target = "clientName")
    @Mapping(source = "clientSecret", target = "clientSecret")
    @Mapping(source = "realm", target = "realm")
    Client toClientDao(ClientDto clientDto);

    @Mapping(source = "clientName", target = "clientName")
    @Mapping(source = "clientSecret", target = "clientSecret")
    @Mapping(source = "realm", target = "realm")
    ClientDto toClientDto(Client client);

    List<ClientDto> toClientDtoList(List<Client> list);
}
