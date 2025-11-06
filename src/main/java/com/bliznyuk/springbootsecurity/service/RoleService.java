package com.bliznyuk.springbootsecurity.service;

import com.bliznyuk.springbootsecurity.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    Optional<Role> findByName(String name);
    void save(Role role);
    List<Role> findAll();
    List<Role> findAllById(Set<Long> roleIds);
}
