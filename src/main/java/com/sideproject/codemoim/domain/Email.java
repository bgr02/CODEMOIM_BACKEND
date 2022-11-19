package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(unique = true)
    private String secretKey;
    @Column
    private LocalDateTime expiredDate;

    @Builder
    public Email(Long id, User user, String email, String secretKey, LocalDateTime expiredDate) {
        this.id = id;
        this.user = user;
        this.email = email;
        this.secretKey = secretKey;
        this.expiredDate = expiredDate;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void updateExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }
}
