package com.antonio.authserver.service;

import com.antonio.authserver.dto.GroupDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.entity.UserGroup;
import com.antonio.authserver.mapper.GroupMapper;
import com.antonio.authserver.mapper.GroupMapperClass;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.AppUserRepository;
import com.antonio.authserver.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {
    private GroupRepository groupRepository;
    private GroupMapper groupMapper;
    private GroupMapperClass groupMapperClass;
    private AppUserRepository appUserRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMapperClass groupMapperClass, AppUserRepository appUserRepository) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupMapperClass = groupMapperClass;
        this.appUserRepository = appUserRepository;
    }

    public List<GroupDto> findAllGroups() {
        return groupMapperClass.daoListToDto(groupRepository.findAll());
    }

    public void createGroup(UserGroup userGroup) {
        Optional<UserGroup> byGroupName = groupRepository.findByName(userGroup.getName());
        if (byGroupName.isPresent())
            throw new CustomException(
                    "Group with the name [ " + byGroupName.get().getName() + " ] already exists!",
                    HttpStatus.CONFLICT);
        else if (userGroup.getName().equals("")) {
            throw new CustomException("The inserted group cannot be null!", HttpStatus.BAD_REQUEST);
        } else {

            groupRepository.save(userGroup);
        }
    }

    public void deleteGroupByName(String name) {
        if (groupRepository.findByName(name).isPresent()) {
            groupRepository.deleteByName(name);
        } else {
            throw new CustomException(
                    "Group with name [ " + groupRepository.findByName(name) + "does not exist", HttpStatus.BAD_REQUEST
            );
        }
    }

    public GroupDto findGroupByName(String name) {
        Optional<UserGroup> byGroupName = groupRepository.findByName(name);
        if (byGroupName.isPresent()) {
            return groupMapperClass.daoToDto(byGroupName.get());
        } else {
            throw new CustomException(
                    "Group with the name [ " + byGroupName.get().getName() + " ] does not exists!",
                    HttpStatus.CONFLICT);
        }
    }

    public void updateByName(String name, GroupDto group) {
        Optional<UserGroup> oldGroup = groupRepository.findByName(name);

        if (oldGroup.isPresent()) {
            if (!group.getName().isEmpty() || !group.getName().equals(" ")) {
                oldGroup.get().setName(group.getName());
            }
            if (!group.getUsers().isEmpty() || group.getUsers().size() > 0) {
                oldGroup.get().setAppUserGroup(group.getUsers());
            }
        }


    }

    public void addRoleForGroup(String name, Role role) {
        Optional<UserGroup> group = groupRepository.findByName(name);
        if (group.isPresent()) {
            List<AppUser> users = group.get().getAppUserGroup();
            for (AppUser user : users) {
                if (!user.getRoles().contains(role)) {
                    user.getRoles().add(role);
                    appUserRepository.save(user);
                }
            }
        }
    }

    public void addUserToGroup(String name, AppUser user) {
        Optional<UserGroup> group = groupRepository.findByName(name);
        group.ifPresent(g -> g.getAppUserGroup().add(user));
        groupRepository.save(group.get());

    }

}
