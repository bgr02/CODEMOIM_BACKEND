package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Role;
import com.sideproject.codemoim.domain.RoleName;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.exception.WithdrawalAccountException;
import com.sideproject.codemoim.repository.UserRepository;
import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("로그인 유저 조회 성공 테스트")
    void loadUserByUsernameTest_success() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        List<Role> roleInfo = new ArrayList<>();
        roleInfo.add(role);

        Collection<? extends GrantedAuthority> roles = roleInfo.stream().map(role1 -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toList());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User("tester", "1234", roles);

        User user = User.builder()
                .username("tester")
                .password("1234")
                .status((byte) 0)
                .roles(roleInfo)
                .build();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        UserDetails userDetails1 = customUserDetailsService.loadUserByUsername(anyString());

        Assertions.assertEquals(userDetails1.getPassword(), "1234");
    }

    @Test
    @DisplayName("로그인 유저 조회 실패 테스트")
    void loadUserByUsernameTest_fail() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        List<Role> roleInfo = new ArrayList<>();
        roleInfo.add(role);

        Collection<? extends GrantedAuthority> roles = roleInfo.stream().map(role1 -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toList());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User("tester", "1234", roles);

        User user = User.builder()
                .username("tester")
                .password("1234")
                .status((byte) 0)
                .roles(roleInfo)
                .build();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(anyString());
        });

        Assertions.assertThrows(WithdrawalAccountException.class, () -> {
            user.updateStatus((byte) 2);
            given(userRepository.findByUsername(anyString())).willReturn(Optional.ofNullable(user));

            customUserDetailsService.loadUserByUsername(anyString());
        });
    }

}