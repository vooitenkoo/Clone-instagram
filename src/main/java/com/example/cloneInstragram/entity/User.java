package com.example.cloneInstragram.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Use only latin letters.")
    private String name;
    @NotBlank(message = "Username cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Use only latin letters.")
    private String username;
    @NotBlank(message = "Password cannot be empty")
    private String password;


    private String profilePicture;

    @Size(min =0,max = 200)
    private String bio;

    @Email(message = "Email is not corrective")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Верните роли или полномочия пользователя
        return getRoles();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Геттеры и сеттеры для остальных полей
}


