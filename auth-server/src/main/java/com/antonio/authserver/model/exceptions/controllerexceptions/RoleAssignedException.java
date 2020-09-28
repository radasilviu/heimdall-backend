package com.antonio.authserver.model.exceptions.controllerexceptions;

import com.antonio.authserver.entity.AppUser;

import java.util.List;

public class RoleAssignedException extends RuntimeException {
    public RoleAssignedException(String name, List<AppUser> usersWithRole) {
        super("Please make sure that all users don't have the role [ " + name + " ] assigned to it \n" + "Users that might have the roles are: [ " + usersWithRole.toString() + " ]");
    }


}



