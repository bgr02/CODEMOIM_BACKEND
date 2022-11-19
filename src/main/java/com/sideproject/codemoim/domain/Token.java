package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false, length = 300)
    private String refreshToken;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime issueDate;
    @Column(nullable = false, updatable = false)
    private LocalDateTime expiredDate;
    @Column(nullable = false)
    private String provider;

    @Builder
    public Token(User user, String refreshToken, LocalDateTime expiredDate, String provider) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expiredDate = expiredDate;
        this.provider = provider;
    }

}
