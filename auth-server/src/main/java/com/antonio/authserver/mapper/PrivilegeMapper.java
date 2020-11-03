package com.antonio.authserver.mapper;
import com.antonio.authserver.dto.PrivilegeDto;
import com.antonio.authserver.entity.Privilege;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
@Mapper(componentModel = "spring",uses = {RoleMapper.class},injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PrivilegeMapper {

    PrivilegeDto toPrivilegeDto(Privilege privilege);
    List<PrivilegeDto> toPrivilegeDtoList(List<Privilege> privileges);
    Set<PrivilegeDto> toPrivilegeDtoSet(Set<Privilege> privilegeSet);
}
