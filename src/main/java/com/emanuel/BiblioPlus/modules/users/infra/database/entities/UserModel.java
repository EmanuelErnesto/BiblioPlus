package com.emanuel.BiblioPlus.modules.users.infra.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "tb_users")
public class UserModel implements UserDetails {


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER"));
        }

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public UserModel() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TokenModel> tokens = new HashSet<>();

    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "cep", nullable = false)
    private String cep;

    @Column(name = "patio")
    private String patio;

    @Column(name = "complement")
    private String complement;

    @Column(name = "neighborhood")
    private String neighborhood;

    @Column(name = "locality")
    private String locality;

    @Column(name = "uf")
    private String uf;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
