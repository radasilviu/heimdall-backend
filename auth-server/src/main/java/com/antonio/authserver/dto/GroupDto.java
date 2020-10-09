package com.antonio.authserver.dto;

import com.antonio.authserver.entity.AppUser;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
    private String name;
    private List<AppUserDto> users;

}
