package com.bliznyuk.springbootsecurity.util;

import com.bliznyuk.springbootsecurity.model.Role;
import com.bliznyuk.springbootsecurity.model.User;
import com.bliznyuk.springbootsecurity.service.RoleService;
import com.bliznyuk.springbootsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class AdminRoleInitializer implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRoleInitializer(
            UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) {
        addRoleIfNotExists("ROLE_USER");
        addRoleIfNotExists("ROLE_ADMIN");

        if (!userService.existsByRolesName("ROLE_ADMIN")) {
            Role adminRole = roleService.findByName("ROLE_ADMIN").orElseThrow();
            Role userRole = roleService.findByName("ROLE_USER").orElseThrow();
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);

            User defaultAdminUser = new User("admin", "admin", "admin@mail.ru", 30,
                    "admin", adminRoles);
            userService.save(defaultAdminUser);
        }
    }

    private void addRoleIfNotExists(String roleName) {
        if (roleService.findByName(roleName).isEmpty()) {
            roleService.save(new Role(roleName));
        }
    }
}