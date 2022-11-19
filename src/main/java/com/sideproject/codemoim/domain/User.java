package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    @Column(nullable = false)
    private Byte status;
    @Column(unique = true)
    private String passwordChangeKey;
    private LocalDateTime passwordChangeKeyExpiredDate;
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @Builder
    public User(String username, String password, Byte status, String passwordChangeKey, LocalDateTime passwordChangeKeyExpiredDate, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.passwordChangeKey = passwordChangeKey;
        this.passwordChangeKeyExpiredDate = passwordChangeKeyExpiredDate;
        this.roles = roles;
    }

    public void updateStatus(byte status) {
        this.status = status;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updatePasswordChangeKey(String passwordChangeKey) {
        this.passwordChangeKey = passwordChangeKey;
    }

    public void updatePasswordChangeKeyExpiredDate(LocalDateTime passwordChangeKeyExpiredDate) {
        this.passwordChangeKeyExpiredDate = passwordChangeKeyExpiredDate;
    }
}
