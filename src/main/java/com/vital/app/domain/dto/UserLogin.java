package com.vital.app.domain.dto;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLogin implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;
    
}
