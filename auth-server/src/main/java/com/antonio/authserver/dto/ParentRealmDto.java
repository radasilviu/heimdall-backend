package com.antonio.authserver.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ParentRealmDto {

    List<AppUserDto> users;

    List<ClientDto> clients;

    List<RoleDto> roles;

    List<GroupDto> groups;



    RealmDto realm;

    public ParentRealmDto(List<AppUserDto> users, List<ClientDto> clients, List<RoleDto> roles, List<GroupDto> groups,RealmDto realm) {
        this.users = users;
        this.clients = clients;
        this.roles = roles;
        this.groups = groups;
        this.realm = realm;
    }

    public ParentRealmDto() {
    }
}
