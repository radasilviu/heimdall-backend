package com.antonio.authserver.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "usergroup")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "userGroupName")
    private String name;

    @OneToMany
    @JoinColumn(name="usergroup")
    private List<AppUser> appUserGroup;

    public UserGroup(){

    }

    public UserGroup(String name, List<AppUser> appUserGroup) {
        this.name = name;
        this.appUserGroup = new ArrayList<>(appUserGroup);
    }

}
