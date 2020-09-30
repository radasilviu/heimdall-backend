package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.IdentityProviderDto;
import com.antonio.authserver.entity.IdentityProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IdentityProviderMapper {

    IdentityProviderMapper INSTANCE = Mappers.getMapper(IdentityProviderMapper.class);

    IdentityProvider toIdentityProviderDao(IdentityProviderDto
                                                   identityProviderDto);

    IdentityProviderDto toIdentityProviderDto(IdentityProvider identityProvider);
}
