package com.bliznyuk.springbootsecurity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table
public class User implements UserDetails {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name = "lastname")
    private String lastName;
    private String email;
    private int age;
    private String password;
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User() {
    }

    public User(String name, String lastName, String email, Set<Role> roles) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    public User(String name, String lastName, String email, int age,
                String password, Set<Role> roles) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.password = password;
        this.roles = roles;
    }

    public String getRoleCorrectNames() {
        return roles.stream().map(s -> s.getName().replace("ROLE_", ""))
                .collect(Collectors.joining(" "));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles != null ? roles.stream()
                .map(s -> new SimpleGrantedAuthority(s.getName()))
                .toList() : Collections.emptyList();
    }

    @Transient
    public String getRoleIdsAsString() {
        if (roles == null || roles.isEmpty()) return "";
        return roles.stream()
                .map(role -> String.valueOf(role.getId()))
                .collect(Collectors.joining(","));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}