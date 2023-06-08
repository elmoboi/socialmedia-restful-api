package com.effectivemobile.socialmedia.security;

import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityRes = Optional.ofNullable(userEntityRepository.findByEmail(email));
        if (userEntityRes.isEmpty())
            throw new UsernameNotFoundException("Could not find User with this email");
        UserEntity userEntity = userEntityRes.get();

        return new User(email, userEntity.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
