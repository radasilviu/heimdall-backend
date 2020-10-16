package com.antonio.authserver.mapper;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.entity.Privilege;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring",uses = {RoleMapper.class},injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PrivilegeMapper {

    Privilege toPrivilegeDao(PrivilegeDto privilegeDto);
    PrivilegeDto toPrivilegeDto(Privilege privilege);
    List<PrivilegeDto> toPrivilegeDtoList(List<Privilege> privileges);
}
