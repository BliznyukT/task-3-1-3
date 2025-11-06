package com.bliznyuk.springbootsecurity.service;


import com.bliznyuk.springbootsecurity.model.User;

import java.util.List;

public interface UserService {
    boolean existsByRolesName(String roleName);
    void updateUser(User user);
    void deleteUserById(long id);
    User getUserById(long id);
    List<User> getUsers();
    void save(User user);
}
