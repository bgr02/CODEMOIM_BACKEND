package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.exception.*;
import com.sideproject.codemoim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileFollowRepository profileFollowRepository;
    private final PostVoteRepository postVoteRepository;
    private final StompService stompService;
    private final CommentVoteRepository commentVoteRepository;
    private final ResourceService resourceService;
    private final NotificationRepository notificationRepository;

    public boolean usernameDuplicateCheck(String username) throws UnsupportedEncodingException {
        String searchUsername = username;

        if (specialWordExist(username)) {
            searchUsername = URLDecoder.decode(searchUsername, "UTF-8");
        }

        Optional<User> optionalUser = Optional.ofNullable(userRepository.usernameDuplicateCheck(searchUsername));

        return optionalUser.isPresent();
    }

    @Transactional
    public void signUp(Map<String, Object> userInfo) {
        User user = registerUser(userInfo);

        if (user != null) {
            registerEmail(user, userInfo);
            registerProfile(user);
        } else {
            throw new DuplicateUsernameException("Duplicate Username.");
        }
    }

    private User registerUser(Map<String, Object> userInfo) {
        String username = (String) userInfo.get("username");

        Optional<User> optionalUser = Optional.ofNullable(userRepository.usernameDuplicateCheck(username));

        final User[] user = new User[1];

        optionalUser.ifPresentOrElse(user1 -> {
            user[0] = null;
        }, () -> {
            List<Role> roles = new ArrayList<>();
            Role role = roleRepository.findByName(RoleName.ROLE_USER);
            roles.add(role);

            User user2 = User.builder()
                    .username((String) userInfo.get("username"))
                    .password(passwordEncoder.encode((String) userInfo.get("password")))
                    .status((byte) 0)
                    .roles(roles)
                    .build();

            user[0] = userRepository.save(user2);
        });

        return user[0];
    }

    private void registerEmail(User user, Map<String, Object> userInfo) {
        String email = (String) userInfo.get("email");

        Optional<Email> optionalEmail = Optional.ofNullable(emailRepository.searchEmailByEmail(email));

        optionalEmail.ifPresentOrElse(email1 -> {
            throw new DuplicateEmailException("Duplicate Email.");
        }, () -> {
            Email email1 = Email.builder()
                    .user(user)
                    .email((String) userInfo.get("email"))
                    .build();

            emailRepository.save(email1);
        });
    }

    private void registerProfile(User user) {
        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.duplicateCheckUsername(user.getUsername()));

        optionalProfile.ifPresentOrElse(profile -> {
            boolean duplicateFlag = true;
            String uniqueUsername = "";

            while(duplicateFlag) {
                String duplicateUsername = profile.getUsername();
                String identifier = UUID.randomUUID().toString().substring(0, 5);
                uniqueUsername = duplicateUsername + "_" + identifier;

                Optional<Profile> optionalProfile2 = Optional.ofNullable(profileRepository.duplicateCheckUsername(uniqueUsername));

                if(optionalProfile2.isEmpty()) {
                    duplicateFlag = false;
                }
            }

            Profile profile1 = Profile.builder()
                    .user(user)
                    .username(uniqueUsername)
                    .contributionPoint(0)
                    .build();

            profileRepository.save(profile1);
        }, () -> {
            Profile profile = Profile.builder()
                    .user(user)
                    .username(user.getUsername())
                    .contributionPoint(0)
                    .build();

            profileRepository.save(profile);
        });
    }

    @Transactional
    public void passwordChange(Map<String, Object> passwordInfo, Long userId) {
        Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(userId);

        optionalUser.ifPresentOrElse(user -> {
            if (passwordEncoder.matches((String) passwordInfo.get("prePassword"), user.getPassword())) {
                user.updatePassword(passwordEncoder.encode((String) passwordInfo.get("newPassword")));
            } else {
                throw new PasswordNotMatchException("Password Not Match.");
            }
        }, () -> {
            throw new UsernameNotFoundException("Not Exist User.");
        });
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public boolean withdrawal(Map<String, Object> passwordInfo, Long userId) {
        User user = userRepository.searchUserByIdAndStatus(userId).orElseThrow(() -> {
            throw new UsernameNotFoundException("Not Exist User");
        });

        if (passwordEncoder.matches((String) passwordInfo.get("password"), user.getPassword())) {
            Profile profile = profileRepository.searchProfileByUserId(user.getId());

            List<PostVote> postVoteList = postVoteRepository.searchPostVoteByProfileId(profile.getId());

            if (!postVoteList.isEmpty()) {
                for (PostVote postVote : postVoteList) {
                    Integer voteCount = postVote.getVoteCount();

                    Profile writer = postVote.getPost().getProfile();

                    int profileId = writer.getId().intValue();
                    int providerId = profile.getId().intValue();

                    if (voteCount > 0 && profileId != providerId) {
                        writer.updateContributionPoint(-1);

                        Map<String, Object> stompMap = new HashMap<>();

                        stompMap.put("profileId", profileId);
                        stompMap.put("providerId", providerId);
                        stompMap.put("postId", postVote.getPost().getId().intValue());

                        stompService.postRecommendCancel(stompMap);
                    } else if (voteCount < 0 && profileId != providerId) {
                        writer.updateContributionPoint(1);
                    }

                    postVoteRepository.delete(postVote);
                }
            }

            List<CommentVote> commentVoteList = commentVoteRepository.searchCommentVoteByProfileId(profile.getId());

            if (!commentVoteList.isEmpty()) {
                for (CommentVote commentVote : commentVoteList) {
                    Integer voteCount = commentVote.getVoteCount();

                    Profile writer = commentVote.getComment().getProfile();

                    int profileId = writer.getId().intValue();
                    int providerId = profile.getId().intValue();

                    if (voteCount > 0 && profileId != providerId) {
                        writer.updateContributionPoint(-1);

                        Map<String, Object> stompMap = new HashMap<>();

                        stompMap.put("profileId", profileId);
                        stompMap.put("providerId", providerId);
                        stompMap.put("commentId", commentVote.getComment().getId().intValue());

                        stompService.commentRecommendCancel(stompMap);
                    } else if (voteCount < 0 && profileId != providerId) {
                        writer.updateContributionPoint(1);
                    }

                    commentVoteRepository.delete(commentVote);
                }
            }

            //포스트 스크랩 취소
            //Collection을 향상된 for문 등으로 요소를 순환 처리하는 과정 중에 요소를 삭제하려고 할 경우 ConcurrentModificationException가 발생한다.
            //해결방법
            //1. Set을 새로운 Set Collection에 복사. 이 방법은 List의 개수가 적거나 보통인 경우 사용, 하지만 개수가 많으면 성능 저하의 원인이 될 수 있습니다.
            //위 방법은 컬렉션 객체를 다시 새로운 Set으로 감쌉니다. 즉, Collection안의 데이터가 변경되서 없어질지라도, iterator하는 것은 new LinkedHashSet()로
            //감싼 객체 링크들이니 문제가 발생하지 않습니다. => ex) Set<Post> scraps = new LinkedHashSet<>(profile.getScraps());
            //2. 동기화 유틸을 이용하는방법. => ex) Set<Post> scraps = Collections.synchronizedSet(new LinkedHashSet<>(profile.getScraps()));
            //3. 이터레이터를 사용
            //ex)
            //Iterator iter = profile.getScraps().iterator();
            //while (iter.hasNext()) {
            //    Post post = (Post) iter.next();
            //
            //    Profile writer = post.getProfile();
            //
            //    if (writer != null) {
            //        writer.updateContributionPoint(-1);
            //    }
            //
            //    iter.remove();
            //}
            Set<Post> scraps = Collections.synchronizedSet(new LinkedHashSet<>(profile.getScraps()));

            if (!scraps.isEmpty()) {
                for (Post post : scraps) {
                    Profile writer = post.getProfile();

                    if (!writer.getId().equals(profile.getId())) {
                        writer.updateContributionPoint(-1);
                    }

                    profile.removePost(post);
                }
            }

            Set<Tag> tags = Collections.synchronizedSet(new LinkedHashSet<>(profile.getTags()));

            if (!tags.isEmpty()) {
                for (Tag tag : tags) {
                    tag.updateProfileFollowerCount(-1);
                    profile.removeTags(tag);
                }
            }

            user.updateStatus((byte) 2);

            if (profile.getProfileImgUrl() != null) {
                String profileImgUrl = profile.getProfileImgUrl();

                Map<String ,Object> imgUrlInfo = new HashMap<>();

                imgUrlInfo.put("imgUrl", profileImgUrl);

                resourceService.delete(imgUrlInfo);

                profile.updateProfileImgUrl(null);
            }

            profileFollowRepository.deleteFollowing(profile);

            profileFollowRepository.deleteFollower(profile);

            notificationRepository.deleteNotificationByProfileAndType(profile, "post_alarm");

            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void keyExpired(Map<String, Object> keyInfo) {
        String key = (String) keyInfo.get("key");

        Optional<User> optionalUser = Optional.ofNullable(userRepository.verifySecretKey(key));

        optionalUser.orElseThrow(() -> {
            throw new InvalidSecretKeyException("Invalid Secret Key");
        });
    }

    @Transactional
    public void findPassword(Map<String, Object> passwordInfo) {
        String key = (String) passwordInfo.get("key");

        Optional<User> optionalUser = Optional.ofNullable(userRepository.verifySecretKey(key));

        optionalUser.ifPresentOrElse(user -> {
            user.updatePassword(passwordEncoder.encode((String) passwordInfo.get("newPassword")));
        }, () -> {
            throw new InvalidSecretKeyException("Invalid Secret Key");
        });
    }

    private boolean specialWordExist(String keyword) {
        boolean exist = false;

        String[] checkWords = {"%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2A", "%2B",
                "%2C", "%2D", "%2E", "%2F", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F", "%40", "%5B", "%5C", "%5D",
                "%5E", "%5F", "%60", "%7B", "%7C", "%7D", "%7E"};

        for (String checkWord : checkWords) {
            if (keyword.contains(checkWord)) {
                exist = true;
                break;
            }
        }

        return exist;
    }

}
