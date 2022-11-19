package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.exception.WithdrawalAccountException;
import com.sideproject.codemoim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        User user = optionalUser.orElseThrow(() -> {
            throw new UsernameNotFoundException(username + "is not exist.");
        });

        if(user.getStatus() == 2) {
            throw new WithdrawalAccountException("This account has previously been withdrawn.");
        }

        Collection<SimpleGrantedAuthority> roles = user.getRoles().stream().map(role -> {
            return new SimpleGrantedAuthority(role.getName().toString());
        }).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(String.valueOf(user.getId()), user.getPassword(), roles);
    }

}
