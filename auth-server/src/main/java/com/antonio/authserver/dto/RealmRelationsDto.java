package com.antonio.authserver.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class RealmRelationsDto {

    private List<AppUserDto> users;
    private List<ClientDto> clients;
    private List<RoleDto> roles;
    private List<GroupDto> groups;
    private RealmDto realm;

    public RealmRelationsDto(List<AppUserDto> users, List<ClientDto> clients, List<RoleDto> roles, List<GroupDto> groups, RealmDto realm) {
        this.users = users;
        this.clients = clients;
        this.roles = roles;
        this.groups = groups;
        this.realm = realm;
    }

    public RealmRelationsDto() {
    }
}
