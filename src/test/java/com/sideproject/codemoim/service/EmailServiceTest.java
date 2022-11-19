package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Email;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.exception.InvalidSecretKeyException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.repository.EmailRepository;
import com.sideproject.codemoim.repository.ProfileRepository;
import com.sideproject.codemoim.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    EmailRepository emailRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    CustomProperties customProperties;

    @Test
    @DisplayName("이메일 중복 체크 성공 테스트")
    void duplicateCheckTest_success() throws UnsupportedEncodingException {
        Email email = Email.builder()
                .email("tester@naver.com")
                .build();

        given(emailRepository.searchEmailByEmail(anyString())).willReturn(email);

        boolean flag = emailService.duplicateCheck("tester@naver.com");

        Assertions.assertTrue(flag);
    }
    
    @Test
    @DisplayName("이메일 중복 체크 실패 테스트")
    void duplicateCheckTest_fail() throws UnsupportedEncodingException {
        Email email = Email.builder()
                .email("tester@naver.com")
                .build();

        given(emailRepository.searchEmailByEmail(anyString())).willReturn(null);

        boolean flag = emailService.duplicateCheck("tester2@naver.com");

        Assertions.assertFalse(flag);
    }
    
    @Test
    @DisplayName("이메일 인증 성공 테스트")
    void sendVerifyEmailTest_success() throws MessagingException {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User profileUser = mock(User.class);

        Profile profile = Profile.builder()
                .user(profileUser)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Email email = Email.builder()
                .user(user)
                .email("tester@naver.com")
                .build();

        Map<String, Object> emailInfo = new HashMap<>();
        emailInfo.put("profileId", 1);
        emailInfo.put("email", "tester@naver.com");

        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.of(user));
        given(emailRepository.searchEmailByUserId(anyLong())).willReturn(email);
        given(emailRepository.searchEmailBySecretKey(anyString())).willReturn(null);

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        CustomProperties testCustomProperties = new CustomProperties(
                new CustomProperties.Token("test" , 10, 10),
                new CustomProperties.Oauth2(new ArrayList<>()),
                new CustomProperties.CookieConfig("http", "root", "back", "front", false, false),
                new CustomProperties.Rabbitmq("localhost", "tester", "1234"));

        when(customProperties.getCookieConfig()).thenReturn(testCustomProperties.getCookieConfig());

        Assertions.assertDoesNotThrow(() -> {
            emailService.sendVerifyEmail(emailInfo, anyLong());
        });
    }

    @Test
    @DisplayName("이메일 인증 실패 테스트")
    void sendVerifyEmailTest_fail() throws MessagingException {
        Email email = Email.builder()
                .email("tester@naver.com")
                .build();

        Map<String, Object> emailInfo = new HashMap<>();

        emailInfo.put("profileId", 1);

        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));
        given(emailRepository.searchEmailByUserId(anyLong())).willReturn(email);
        given(emailRepository.searchEmailBySecretKey(anyString())).willReturn(null);

        CustomProperties testCustomProperties = new CustomProperties(
                new CustomProperties.Token("test" , 10, 10),
                new CustomProperties.Oauth2(new ArrayList<>()),
                new CustomProperties.CookieConfig("http","root", "back", "front", false,false),
                new CustomProperties.Rabbitmq("localhost", "tester", "1234"));

        when(customProperties.getCookieConfig()).thenReturn(testCustomProperties.getCookieConfig());

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            emailService.sendVerifyEmail( emailInfo, anyLong());
        });

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            User profileUser = mock(User.class);

            Profile profile = Profile.builder()
                    .user(profileUser)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            emailService.sendVerifyEmail( emailInfo, anyLong());
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            User profileUser = mock(User.class);

            Profile profile = Profile.builder()
                    .user(profileUser)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            User user = User.builder()
                    .username("tester")
                    .status((byte) 0)
                    .build();

            given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.of(user));

            emailService.sendVerifyEmail( emailInfo, anyLong());
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            User profileUser = mock(User.class);

            Profile profile = Profile.builder()
                    .user(profileUser)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            User user = User.builder()
                    .username("tester")
                    .status((byte) 0)
                    .build();

            given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.of(user));

            emailInfo.put("email", "tester@naver.com");

            emailService.sendVerifyEmail( emailInfo, anyLong());
        });
    }
    
    @Test
    @DisplayName("이메일 비밀키 검증 성공 테스트")
    void verifyKeyTest_success() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        Email email = Email.builder()
                .user(user)
                .email("tester@naver.com")
                .build();

        given(emailRepository.verifySecretKey(anyString())).willReturn(email);

        String key = emailService.verifyKey(anyString());

        Assertions.assertNotNull(key);
    }

    @Test
    @DisplayName("이메일 비밀키 검증 실패 테스트")
    void verifyKeyTest_fail() {
        given(emailRepository.verifySecretKey(anyString())).willReturn(null);

        String key = emailService.verifyKey(anyString());

        Assertions.assertNull(key);
    }
    
    @Test
    @DisplayName("이메일 비밀키 만료 성공 테스트")
    void keyExpireTest_success() {
        Email email = Email.builder()
                .email("tester@naver.com")
                .build();

        given(emailRepository.verifySecretKey(anyString())).willReturn(email);

        Map<String ,Object> keyInfo = new HashMap<>();
        keyInfo.put("key", anyString());

        Assertions.assertDoesNotThrow(() -> {
            emailService.keyExpire(keyInfo);
        });
    }

    @Test
    @DisplayName("이메일 비밀키 만료 실패 테스트")
    void keyExpireTest_fail() {
        given(emailRepository.verifySecretKey(anyString())).willReturn(null);

        Map<String ,Object> keyInfo = new HashMap<>();
        keyInfo.put("key", anyString());

        Assertions.assertThrows(InvalidSecretKeyException.class, () -> {
            emailService.keyExpire(keyInfo);
        });
    }

    @Test
    @DisplayName("비밀번호 찾기 이메일 전송 성공 테스트")
    void sendFindPasswordEmailTest_success() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        Email email = Email.builder()
                .user(user)
                .email("tester@naver.com")
                .build();

        Map<String, Object> emailInfo = new HashMap<>();
        emailInfo.put("email", "tester@naver.com");

        given(emailRepository.searchEmailByEmail(anyString())).willReturn(email);
        given(emailRepository.searchEmailBySecretKey(anyString())).willReturn(null);

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        CustomProperties testCustomProperties = new CustomProperties(
                new CustomProperties.Token("test" , 10, 10),
                new CustomProperties.Oauth2(new ArrayList<>()),
                new CustomProperties.CookieConfig("http", "root", "back", "front", false,false),
                new CustomProperties.Rabbitmq("localhost", "tester", "1234"));

        when(customProperties.getCookieConfig()).thenReturn(testCustomProperties.getCookieConfig());


        Assertions.assertTrue(emailService.sendFindPasswordEmail(emailInfo));
    }

    @Test
    @DisplayName("비밀번호 찾기 이메일 전송 실패 테스트")
    void sendFindPasswordEmailTest_fail() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        Email email = Email.builder()
                .user(user)
                .email("tester@naver.com")
                .build();

        Map<String, Object> emailInfo = new HashMap<>();

        given(emailRepository.searchEmailByEmail(null)).willReturn(email);
        given(emailRepository.searchEmailBySecretKey(anyString())).willReturn(null);

        CustomProperties testCustomProperties = new CustomProperties(
                new CustomProperties.Token("test" , 10, 10),
                new CustomProperties.Oauth2(new ArrayList<>()),
                new CustomProperties.CookieConfig("http", "root", "back", "front", false,false),
                new CustomProperties.Rabbitmq("localhost", "tester", "1234"));

        when(customProperties.getCookieConfig()).thenReturn(testCustomProperties.getCookieConfig());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendFindPasswordEmail(emailInfo);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            emailInfo.put("email", "tester@naver.com");
            given(emailRepository.searchEmailByEmail(anyString())).willReturn(email);

            emailService.sendFindPasswordEmail(emailInfo);
        });
    }

}
