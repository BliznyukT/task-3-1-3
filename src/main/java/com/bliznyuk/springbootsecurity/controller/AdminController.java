package com.bliznyuk.springbootsecurity.controller;


import com.bliznyuk.springbootsecurity.model.Role;
import com.bliznyuk.springbootsecurity.model.User;
import com.bliznyuk.springbootsecurity.service.RoleService;
import com.bliznyuk.springbootsecurity.service.UserService;
import com.bliznyuk.springbootsecurity.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, UserServiceImpl userServiceImpl) {
        this.userService = userService;
        this.roleService = roleService;
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping
    public String showAdmin(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("users", userService.getUsers());
        return "admin/admin";
    }

    @PostMapping
    public String saveUser(User newUser, @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        Set<Role> roles;
        if (roleIds != null && !roleIds.isEmpty()) {
            roles = new HashSet<>(roleService.findAllById(roleIds));
        } else {
            Role defaultRole = roleService.findByName("ROLE_USER").orElseThrow();
            roles = new HashSet<>();
            roles.add(defaultRole);
        }
        newUser.setRoles(roles);
        userService.save(newUser);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") int id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user,
                             @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        Set<Role> roles;
        if (roleIds != null && !roleIds.isEmpty()) {
            roles = new HashSet<>(roleService.findAllById(roleIds));
        } else {
            roles = new HashSet<>();
            roles.add(roleService.findByName("ROLE_USER").orElseThrow());
        }
        user.setRoles(roles);
        userService.updateUser(user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        if (currentEmail.equals(user.getEmail())) {
            UserDetails updUserDetails = userServiceImpl.loadUserByUsername(user.getEmail());

            if (user.getRoles().stream().noneMatch(s -> s.getName().equals("ROLE_ADMIN"))) {
                SecurityContextHolder.clearContext();
                return "redirect:/auth/login?logout";
            }

            Authentication newAuth = new UsernamePasswordAuthenticationToken(updUserDetails,
                    auth.getCredentials(), updUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        return "redirect:/admin";
    }
}