package com.vishal.mockapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vishal.mockapi.entity.UserEntity;
import com.vishal.mockapi.repository.UserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserEntity> users = userRepo.findByEmail(username);
        if (!users.isEmpty()) {
            UserEntity user = users.get(0);
            return new User(username, user.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole())));
        }
        throw new UsernameNotFoundException("Invalid login attempt!");
    }

}