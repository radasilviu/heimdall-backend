package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.RealmDto;
import com.antonio.authserver.entity.Realm;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RealmMapper {

    RealmMapper INSTANCE = Mappers.getMapper(RealmMapper.class);

    Realm toRealmDao(RealmDto realmDto);
    RealmDto toRealmDto(Realm realm);
    List<RealmDto> toRealmDtoList(List<Realm> list);
}
