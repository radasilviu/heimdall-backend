package com.antonio.authserver.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    @OneToMany
    private List<AppUser> appUser;
}
