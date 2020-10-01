package com.antonio.authserver.mapper;

import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;

@Mapper(componentModel = "spring",injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClientMapper {

    Client toClientDao(ClientDto clientDto);
    ClientDto toClientDto(Client client);
    List<ClientDto> toClientDtoList(List<Client> list);
}
