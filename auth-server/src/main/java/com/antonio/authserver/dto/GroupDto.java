package com.antonio.authserver.dto;

import com.antonio.authserver.entity.AppUser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Data
public class GroupDto {
    private String name;
    private List<AppUser> users;
}
