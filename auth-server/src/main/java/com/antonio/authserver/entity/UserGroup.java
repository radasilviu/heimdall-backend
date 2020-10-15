package com.antonio.authserver.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Realm realm;

    public UserGroup(){

    }

    public UserGroup(String name, List<AppUser> appUserGroup, Realm realm) {
        this.name = name;
        this.appUserGroup = new ArrayList<>(appUserGroup);
        this.realm = realm;
    }

}
