package com.antonio.authserver.service;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.GroupDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.entity.Role;
import com.antonio.authserver.entity.UserGroup;
import com.antonio.authserver.mapper.AppUserMapper;
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
    private UserService userService;
    private AppUserMapper appUserMapper;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, GroupMapperClass groupMapperClass, AppUserRepository appUserRepository, UserService userService, AppUserMapper appUserMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.groupMapperClass = groupMapperClass;
        this.appUserRepository = appUserRepository;
        this.userService = userService;
        this.appUserMapper = appUserMapper;
    }

    public List<GroupDto> findAllGroups(String name) {
        return groupMapperClass.daoListToDto(groupRepository.findAllByRealmName(name));
    }

    public void createGroup(UserGroup userGroup) {
        Optional<UserGroup> byGroupName = groupRepository.findByNameAndRealmName(userGroup.getName(), userGroup.getRealm().getName());

        if (byGroupName.isPresent())
            throw new CustomException(
                    "Group with the name [ " + byGroupName.get().getName() + " ] already exists!",
                    HttpStatus.CONFLICT);
        else if (userGroup.getName().equals("") || userGroup.getName().matches("^\\s*$")){
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
                    "Group with name [ " + name + "does not exist", HttpStatus.BAD_REQUEST
            );
        }
    }

    public GroupDto findGroupByName(String name, String realmName) {
        Optional<UserGroup> byGroupName = groupRepository.findByNameAndRealmName(name, realmName);

        if (byGroupName.isPresent()) {
            return groupMapperClass.daoToDto(byGroupName.get());
        } else {
            throw new CustomException(
                    "Group with the name [ " + name + " ] does not exists!",
                    HttpStatus.CONFLICT);
        }
    }

    public void updateByName(String name, GroupDto group) {
       UserGroup oldGroup = groupRepository.findByName(name).orElseThrow(() -> new CustomException("Group with name " + name + " not found", HttpStatus.BAD_REQUEST));
       UserGroup newGroup = groupMapperClass.dtoToDao(group);
        if (!group.getName().isEmpty() || !group.getName().equals(" ")) {
            oldGroup.setName(group.getName());
        }
        if (!group.getUsers().isEmpty() || group.getUsers().size() > 0) {

            oldGroup.setAppUserGroup(newGroup.getAppUserGroup());
        }
       groupRepository.save(oldGroup);
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

    public List<AppUserDto> getUsersFromGroup(String groupName) {
        UserGroup userGroup = groupRepository.findByName(groupName).orElseThrow(() -> new CustomException("Group with name" + groupName + " not found", HttpStatus.BAD_REQUEST));
        List<AppUser> appUsers = userGroup.getAppUserGroup();
        return appUserMapper.toAppUserDtoList(appUsers);
    }

    public void deleteUserFromGroupByName(String groupName, String username) {
        UserGroup userGroup = groupRepository
                .findByName(groupName)
                .orElseThrow(() -> new CustomException("Group with name" + groupName + " not found", HttpStatus.BAD_REQUEST));

        userGroup.getAppUserGroup().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .ifPresent(u -> userGroup.getAppUserGroup().remove(u));
        groupRepository.save(userGroup);
    }

}
