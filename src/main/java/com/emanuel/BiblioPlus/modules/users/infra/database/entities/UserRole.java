package com.emanuel.BiblioPlus.modules.users.infra.database.entities;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("admin"),
    CLIENT("client");

    private String role;

    UserRole(String role){
        this.role = role;
    }

}
