package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.FollowRelationDto;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.dto.ProfileDetailInfoDto;
import com.sideproject.codemoim.dto.ProfileDto;
import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.exception.TagNotFoundException;
import com.sideproject.codemoim.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final OauthInfoRepository oauthInfoRepository;
    private final TagRepository tagRepository;
    private final ResourceService resourceService;
    private final ProfileFollowRepository profileFollowRepository;
    private final PostRepository postRepository;

    public ProfileDto searchProfileByUserId(Long userId) {
        ProfileDto profileDto = profileRepository.searchProfileDtoByUserId(userId);

        List<Role> roles = roleRepository.searchRoleByUserId(userId);
        profileDto.setUserRoles(roles.stream().map(role -> role.getName().toString()).collect(Collectors.toList()));

        List<Tag> tags = profileRepository.searchProfileFollowTags(profileDto.getId());

        if (!tags.isEmpty()) {
            profileDto.setFollowTags(tags.stream().map(tag -> tag.getName()).collect(Collectors.toList()));
        }

        List<Profile> profileList = profileFollowRepository.searchFollowingByProfileId(profileDto.getId());

        if (!profileList.isEmpty()) {
            profileDto.setFollowingProfiles(profileList.stream().map(profile -> profile.getId()).collect(Collectors.toList()));
        }

        Optional<Email> email = Optional.ofNullable(emailRepository.searchEmailByUserId(userId));

        email.ifPresentOrElse(emailEntity -> {
            profileDto.setEmail(emailEntity.getEmail());
        }, () -> {
            Optional<OauthInfo> optionalOauthInfo = oauthInfoRepository.findByUserId(userId);

            optionalOauthInfo.ifPresent(oauthInfo -> {
                profileDto.setEmail("");
            });
        });

        return profileDto;
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public void modifyProfile(Map<String, Object> profileInfo, Long userId) {
        Profile profile = profileRepository.searchProfileByUserId(userId);
        Email email = emailRepository.searchEmailByUserId(userId);
        Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(userId);

        Optional<String> usernameOptional = Optional.ofNullable((String) profileInfo.get("username"));
        Optional<String> emailOptional = Optional.ofNullable((String) profileInfo.get("email"));
        Optional<String> profileUrlOptional = Optional.ofNullable((String) profileInfo.get("profileUrl"));

        usernameOptional.ifPresent(updateUsername -> {
            profile.updateUsername(updateUsername);
        });

        emailOptional.ifPresent(updateEmail -> {
            email.updateEmail(updateEmail);

            optionalUser.ifPresent(user -> {
                user.updateStatus((byte) 0);
            });
        });

        profileUrlOptional.ifPresent(updateProfileImgUrl -> {
            if (profile.getProfileImgUrl() != null) {
                String profileImgUrl = profile.getProfileImgUrl();

                Map<String ,Object> imgUrlInfo = new HashMap<>();

                imgUrlInfo.put("imgUrl", profileImgUrl);

                resourceService.delete(imgUrlInfo);
            }

            profile.updateProfileImgUrl(updateProfileImgUrl);
        });
    }

    public boolean duplicateCheckUsername(String username) throws UnsupportedEncodingException {
        String searchUsername = username;

        if (specialWordExist(username)) {
            searchUsername = URLDecoder.decode(searchUsername, "UTF-8");
        }

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.duplicateCheckUsername(searchUsername));

        return optionalProfile.isPresent();
    }

    public List<ProfileDto> searchProfileRank() {
        return profileRepository.searchProfileRank();
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public boolean followTag(Map<String, Object> followInfo, Long userId) {
        long id = (int) followInfo.get("id");
        String tagName = (String) followInfo.get("tagName");
        final boolean[] followFlag = {false};

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(id));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                String changeTagName = tagName;

                if (specialWordExist(tagName)) {
                    try {
                        changeTagName = URLDecoder.decode(tagName, "UTF-8");
                    } catch (UnsupportedEncodingException exception) {
                        StackTraceElement[] ste = exception.getStackTrace();

                        String className = ste[0].getClassName();
                        String methodName = ste[0].getMethodName();
                        int lineNumber = ste[0].getLineNumber();
                        String fileName = ste[0].getFileName();

                        log.error("UnsupportedEncodingException: " + exception.getMessage());
                        log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);
                    }
                }

                Optional<Tag> optionalTag = Optional.ofNullable(tagRepository.findByName(changeTagName));

                Tag tag = optionalTag.orElseThrow(() -> {
                    throw new TagNotFoundException("Tag Not Found");
                });

                boolean tagExist = profileRepository.searchFollowTagExist(id, tag.getId());

                if (!tagExist) {
                    tag.updateProfileFollowerCount(1);

                    profile.updateTags(tag);

                    followFlag[0] = true;
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return followFlag[0];
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public boolean unfollowTag(Map<String, Object> followInfo, Long userId) throws UnsupportedEncodingException {
        long id = (int) followInfo.get("id");
        String tagName = (String) followInfo.get("tagName");
        final boolean[] unfollowFlag = {false};

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(id));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                String changeTagName = tagName;

                if (specialWordExist(tagName)) {
                    try {
                        changeTagName = URLDecoder.decode(tagName, "UTF-8");
                    } catch (UnsupportedEncodingException exception) {
                        StackTraceElement[] ste = exception.getStackTrace();

                        String className = ste[0].getClassName();
                        String methodName = ste[0].getMethodName();
                        int lineNumber = ste[0].getLineNumber();
                        String fileName = ste[0].getFileName();

                        log.error("UnsupportedEncodingException: " + exception.getMessage());
                        log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);
                    }
                }

                Optional<Tag> optionalTag = Optional.ofNullable(tagRepository.findByName(changeTagName));

                Tag tag = optionalTag.orElseThrow(() -> {
                    throw new TagNotFoundException("Tag Not Found");
                });

                boolean tagExist = profileRepository.searchFollowTagExist(id, tag.getId());

                if (tagExist) {
                    tag.updateProfileFollowerCount(-1);

                    profile.removeTags(tag);

                    unfollowFlag[0] = true;
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return unfollowFlag[0];
    }

    public Map<String, Object> searchProfileTotalInfo(Long id) {
        boolean profileFlag = profileRepository.validateProfile(id);

        if (profileFlag) {
            Map<String, Object> profileTotalInfo = new HashMap<>();

            ProfileDetailInfoDto profileDetailInfoDto = profileRepository.searchProfileInfo(id);

            profileTotalInfo.put("profileDetailInfo", profileDetailInfoDto);

            Pageable pageable = PageRequest.of(0, 5);

            Page<FollowRelationDto> followingInfo = profileRepository.searchFollowingInfo(pageable, id);

            profileTotalInfo.put("followingInfo", followingInfo);

            Page<PostDto> postInfo = postRepository.searchPostListByProfileId(pageable, id);

            profileTotalInfo.put("postInfo", postInfo);

            return profileTotalInfo;
        } else {
            return null;
        }
    }

    public Page<FollowRelationDto> searchFollowingInfo(Pageable pageable, Long id) {
        return profileRepository.searchFollowingInfo(pageable, id);
    }

    public Page<FollowRelationDto> searchFollowerInfo(Pageable pageable, Long id) {
        return profileRepository.searchFollowerInfo(pageable, id);
    }

    public Page<FollowRelationDto> searchTagInfo(Pageable pageable, Long id) {
        Profile profile = profileRepository.searchProfileById(id);

        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();
        int totalCount = profile.getTags().size();
        List<FollowRelationDto> followRelationDtoList = new ArrayList<>();

        if (totalCount > 5) {
            followRelationDtoList = profile.getTags().stream().skip(offset).limit(pageSize).map(tag -> {
                FollowRelationDto followRelationDto = new FollowRelationDto();

                followRelationDto.setId(tag.getId());
                followRelationDto.setName(tag.getName());

                if (tag.getTagImgUrl() != null) {
                    followRelationDto.setImgUrl(tag.getTagImgUrl());
                }

                return followRelationDto;
            }).collect(Collectors.toList());
        } else if (totalCount > 0) {
            followRelationDtoList = profile.getTags().stream().map(tag -> {
                FollowRelationDto followRelationDto = new FollowRelationDto();

                followRelationDto.setId(tag.getId());
                followRelationDto.setName(tag.getName());

                if (tag.getTagImgUrl() != null) {
                    followRelationDto.setImgUrl(tag.getTagImgUrl());
                }

                return followRelationDto;
            }).collect(Collectors.toList());
        }

        return new PageImpl<>(followRelationDtoList, pageable, totalCount);
    }

    @Transactional
    public boolean followProfile(Map<String, Object> followInfo, Long userId) {
        long profileId = (int) followInfo.get("profileId");
        long followingId = (int) followInfo.get("followingId");
        final boolean[] followFlag = {false};

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Profile followingProfile = profileRepository.searchProfileById(followingId);

                ProfileFollow profileFollow = ProfileFollow.builder()
                        .profile(profile)
                        .following(followingProfile)
                        .build();

                ProfileFollow saveProfileFollow = profileFollowRepository.save(profileFollow);

                if (saveProfileFollow.getId() != null) {
                    followFlag[0] = true;
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return followFlag[0];
    }

    @Transactional
    public boolean unfollowProfile(Map<String, Object> unfollowInfo, Long userId) {
        long profileId = (int) unfollowInfo.get("profileId");
        long followingId = (int) unfollowInfo.get("followingId");
        final boolean[] unfollowFlag = {false};

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                ProfileFollow profileFollow = profileFollowRepository.searchFollowingProfile(profileId, followingId);

                if (profileFollow != null) {
                    profileFollowRepository.delete(profileFollow);
                    unfollowFlag[0] = true;
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return unfollowFlag[0];
    }

    private boolean specialWordExist(String target) {
        boolean exist = false;

        String[] checkWords = {"%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2A", "%2B",
                "%2C", "%2D", "%2E", "%2F", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F", "%40", "%5B", "%5C", "%5D",
                "%5E", "%5F", "%60", "%7B", "%7C", "%7D", "%7E"};

        for (String checkWord : checkWords) {
            if (target.contains(checkWord)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
