package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.exception.DuplicateUsernameException;
import com.sideproject.codemoim.exception.InvalidSecretKeyException;
import com.sideproject.codemoim.exception.PasswordNotMatchException;
import com.sideproject.codemoim.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    EmailRepository emailRepository;

    @Mock
    PostVoteRepository postVoteRepository;

    @Mock
    CommentVoteRepository commentVoteRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    ProfileFollowRepository profileFollowRepository;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    StompService stompService;

    @Test
    @DisplayName("유저 아이디 기반 검색 성공 테스트")
    void searchUserByIdTest_success() {
        User user = User.builder()
                .build();

        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.of(user));

        Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(anyLong());

        Assertions.assertNotNull(optionalUser.get());
    }

    @Test
    @DisplayName("유저 아이디 기반 검색 실패 테스트")
    void searchUserByIdTest_fail() {
        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));

        Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(anyLong());

        Assertions.assertTrue(optionalUser.isEmpty());
    }

    @Test
    @DisplayName("유저 이름 중복 검사 성공 테스트")
    void usernameDuplicateCheckTest_success() throws UnsupportedEncodingException {
        User user = User.builder()
                .build();

        given(userRepository.usernameDuplicateCheck(anyString())).willReturn(user);

        boolean check = userService.usernameDuplicateCheck(anyString());

        Assertions.assertTrue(check);
    }

    @Test
    @DisplayName("유저 이름 중복 검사 실패 테스트")
    void usernameDuplicateCheckTest_fail() throws UnsupportedEncodingException {
        given(userRepository.usernameDuplicateCheck(anyString())).willReturn(null);

        boolean check = userService.usernameDuplicateCheck(anyString());

        Assertions.assertFalse(check);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void singUpTest_success() {
        Role role = Role.builder()
                .build();

        Email email = Email.builder()
                .build();

        User user = User.builder()
                .username("tester")
                .build();

        given(roleRepository.findByName(any())).willReturn(role);
        given(userRepository.usernameDuplicateCheck(anyString())).willReturn(null);
        given(userRepository.save(any())).willReturn(user);
        given(emailRepository.searchEmailByEmail(anyString())).willReturn(null);
        given(profileRepository.duplicateCheckUsername(anyString())).willReturn(null);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", "tester");
        userInfo.put("password", "1234");
        userInfo.put("email", "tester@naver.com");

        Assertions.assertDoesNotThrow(() -> {
            userService.signUp(userInfo);
        });
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    void singUpTest_fail() {
        Role role = Role.builder()
                .build();

        Email email = Email.builder()
                .build();

        given(roleRepository.findByName(any())).willReturn(role);
        given(userRepository.usernameDuplicateCheck(anyString())).willReturn(null);
        given(userRepository.save(any())).willReturn(null);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", "tester");
        userInfo.put("password", "1234");
        userInfo.put("email", "tester@naver.com");

        Assertions.assertThrows(DuplicateUsernameException.class, () -> {
            userService.signUp(userInfo);
        });
    }

    @Test
    @DisplayName("비밀번호 변경 성공 테스트")
    void passwordChangeTest_success() {
        User user = User.builder()
                .password("1234")
                .build();

        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        Map<String, Object> passwordInfo = new HashMap<>();
        passwordInfo.put("prePassword", "1234");
        passwordInfo.put("newPassword", "123123");

        Assertions.assertDoesNotThrow(() -> {
            userService.passwordChange(passwordInfo, anyLong());
        });
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트")
    void passwordChangeTest_fail() {
        User user = User.builder().build();

        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(user));

        Map<String, Object> passwordInfo = new HashMap<>();
        passwordInfo.put("prePassword", "1234");
        passwordInfo.put("newPassword", "5678");

        Assertions.assertThrows(PasswordNotMatchException.class, () -> {
            userService.passwordChange(passwordInfo, anyLong());
        });
    }

    @Test
    @DisplayName("회원탈퇴 성공 테스트")
    void withdrawalTest_success() {
        User user = mock(User.class);
        given(user.getPassword()).willReturn("1234");

        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        Profile profile = mock(Profile.class);
        given(profileRepository.searchProfileByUserId(anyLong())).willReturn(profile);
        when(profile.getId()).thenReturn(1L);

        Profile votePostWriter = mock(Profile.class);

        Post votePost = mock(Post.class);
        when(votePost.getProfile()).thenReturn(votePostWriter);

        PostVote postVote = PostVote.builder()
                .post(votePost)
                .voteCount(1)
                .build();

        List<PostVote> postVoteList = new ArrayList<>();
        postVoteList.add(postVote);

        given(postVoteRepository.searchPostVoteByProfileId(profile.getId())).willReturn(postVoteList);

        Profile commentWriter = mock(Profile.class);

        Comment comment = Comment.builder()
                .profile(commentWriter)
                .build();

        CommentVote commentVote = CommentVote.builder()
                .comment(comment)
                .voteCount(0)
                .build();

        List<CommentVote> commentVoteList = new ArrayList<>();
        commentVoteList.add(commentVote);

        given(commentVoteRepository.searchCommentVoteByProfileId(profile.getId())).willReturn(commentVoteList);

        Profile writer = mock(Profile.class);

        Post post = Post.builder()
                .profile(writer)
                .build();

        Set<Post> scraps = new LinkedHashSet<>();
        scraps.add(post);

        when(profile.getScraps()).thenReturn(scraps);

        Tag tag = Tag.builder()
                .profileFollowerCount(0)
                .build();

        Set<Tag> tags = new LinkedHashSet<>();
        tags.add(tag);

        when(profile.getTags()).thenReturn(tags);

        Map<String, Object> passwordInfo = new HashMap<>();
        passwordInfo.put("password", "1234");

        Assertions.assertDoesNotThrow(() -> {
            userService.withdrawal(passwordInfo, anyLong());
        });
    }

    @Test
    @DisplayName("회원탈퇴 실패 테스트")
    void withdrawalTest_fail() {
        Map<String, Object> passwordInfo = new HashMap<>();
        passwordInfo.put("password", "1234");

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.withdrawal(passwordInfo, anyLong());
        });
    }

    @Test
    @DisplayName("유저 비밀번호 변경키 만료여부 확인 성공 테스트")
    void keyExpiredTest_success() {
        User user = User.builder().build();

        given(userRepository.verifySecretKey(anyString())).willReturn(user);

        Map<String, Object> keyInfo = new HashMap<>();
        keyInfo.put("key", anyString());

        Assertions.assertDoesNotThrow(() -> {
            userService.keyExpired(keyInfo);
        });
    }

    @Test
    @DisplayName("유저 비밀번호 변경키 만료여부 확인 실패 테스트")
    void keyExpiredTest_failure() {
        given(userRepository.verifySecretKey(anyString())).willReturn(null);

        Map<String, Object> keyInfo = new HashMap<>();
        keyInfo.put("key", anyString());

        Assertions.assertThrows(InvalidSecretKeyException.class, () -> {
            userService.keyExpired(keyInfo);
        });
    }

    @Test
    @DisplayName("비밀번호 찾기를 통한 비밀번호 변경 성공 테스트")
    void findPasswordTest_success() {
        User user = User.builder().build();

        given(userRepository.verifySecretKey(anyString())).willReturn(user);

        Map<String, Object> keyInfo = new HashMap<>();
        keyInfo.put("key", "random key");

        Assertions.assertDoesNotThrow(() -> {
            userService.findPassword(keyInfo);
        });
    }

    @Test
    @DisplayName("비밀번호 찾기를 통한 비밀번호 변경 실패 테스트")
    void findPasswordTest_fail() {
        given(userRepository.verifySecretKey(anyString())).willReturn(null);

        Map<String, Object> keyInfo = new HashMap<>();
        keyInfo.put("key", "random key");

        Assertions.assertThrows(InvalidSecretKeyException.class, () -> {
            userService.findPassword(keyInfo);
        });
    }

}
