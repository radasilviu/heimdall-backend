package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.IdentityProviderDto;
import com.antonio.authserver.entity.IdentityProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AppUserMapper.class})
public interface IdentityProviderMapper {

    IdentityProviderMapper INSTANCE = Mappers.getMapper(IdentityProviderMapper.class);

    @Mapping(target = "users", ignore = true)
    IdentityProvider toIdentityProviderDao(IdentityProviderDto
                                                   identityProviderDto);

    IdentityProviderDto toIdentityProviderDto(IdentityProvider identityProvider);

    List<IdentityProviderDto> toIdentityProviderDtoList(List<IdentityProvider> list);
}
