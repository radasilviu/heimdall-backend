package com.antonio.authserver.mapper;

import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.dto.GroupDto;
import com.antonio.authserver.dto.RealmDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.entity.Group;
import com.antonio.authserver.entity.Realm;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GroupMapper {

    GroupDto toGroupDto(Group group);
    Group toGroupDao(GroupDto groupDto);
    List<GroupDto> toGroupDtoList(List<Group> list);

}
