package com.antonio.authserver.mapper;
import com.antonio.authserver.dto.ResourceDto;
import com.antonio.authserver.entity.Resource;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
@Mapper(componentModel = "spring",uses = {RoleMapper.class},injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ResourceMapper {

    Resource toResourceDao(ResourceDto resourceDto);
    ResourceDto toResourceDto(Resource resource);
    List<ResourceDto> toResourceDtoList(List<Resource> resources);
    Set<ResourceDto> toResourceDtoSet(Set<Resource> resourceSet);
}
