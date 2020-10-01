package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.IdentityProviderDto;
import com.antonio.authserver.entity.IdentityProvider;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface IdentityProviderMapper {


    IdentityProvider toIdentityProviderDao(IdentityProviderDto
                                                   identityProviderDto);

    IdentityProviderDto toIdentityProviderDto(IdentityProvider identityProvider);
}
