package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.Tag;
import com.sideproject.codemoim.dto.TagDetailDto;
import com.sideproject.codemoim.dto.TagDto;
import com.sideproject.codemoim.exception.*;
import com.sideproject.codemoim.repository.PostRepository;
import com.sideproject.codemoim.repository.ProfileRepository;
import com.sideproject.codemoim.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final ResourceService resourceService;

    public boolean duplicateCheckName(String name) throws UnsupportedEncodingException {
        String searchName = name;

        if (specialWordExist(name)) {
            searchName = URLDecoder.decode(searchName, "UTF-8");
        }

        return tagRepository.duplicateCheckName(searchName);
    }

    public TagDto searchInfoTag(Long id) {
        return tagRepository.searchInfoTag(id);
    }

    public boolean searchTagDeletableCheck(Long id) {
        //연관 프로필 탐색
        boolean followTagExist = profileRepository.followTagExist(id);

        //연관 포스트 탐색
        boolean relationTagExist = postRepository.relationTagExist(id);

        return !followTagExist && !relationTagExist;
    }

    @Transactional
    public void createTag(Map<String, Object> tagInfo, Long userId) {
        long profileId = (int) tagInfo.get("profileId");
        String name = (String) tagInfo.get("name");
        Optional<String> optionalTagUrl = Optional.ofNullable((String) tagInfo.get("tagUrl"));

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                boolean nameDuplicateFlag = tagRepository.duplicateCheckName(name);

                if(!nameDuplicateFlag) {
                    Tag tag = Tag.builder()
                            .name(name)
                            .postTagCount(0)
                            .profileFollowerCount(0)
                            .build();

                    optionalTagUrl.ifPresent(tagUrl -> {
                        tag.updateTagImgUrl(tagUrl);
                    });

                    tagRepository.save(tag);
                } else {
                    throw new DuplicateTagException("That tag already exists.");
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public void modifyTag(Map<String, Object> tagInfo, Long userId) {
        Long id = (long) (int) tagInfo.get("id");
        long profileId = (int) tagInfo.get("profileId");

        Optional<String> optionalName = Optional.ofNullable((String) tagInfo.get("name"));
        Optional<String> optionalTagUrl = Optional.ofNullable((String) tagInfo.get("tagUrl"));

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Optional<Tag> optionalTag = tagRepository.findById(id);

                optionalTag.ifPresentOrElse(tag -> {
                    optionalName.ifPresent(name -> {
                        if(!tag.getName().equals(name)) {
                            boolean nameDuplicateFlag = tagRepository.duplicateCheckName(name);

                            if(!nameDuplicateFlag) {
                                tag.updateName(name);
                            } else {
                                throw new DuplicateTagException("That tag already exists.");
                            }
                        }
                    });

                    optionalTagUrl.ifPresent(tagUrl -> {
                        if(tag.getTagImgUrl() != null) {
                            String tagImgUrl = tag.getTagImgUrl();

                            Map<String ,Object> imgUrlInfo = new HashMap<>();

                            imgUrlInfo.put("imgUrl", tagImgUrl);

                            resourceService.delete(imgUrlInfo);
                        }

                        tag.updateTagImgUrl(tagUrl);
                    });
                }, () -> {
                    throw new TagNotFoundException("Tag Not Found");
                });
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

    @Transactional
    public void deleteTag(Map<String, Object> tagInfo, Long userId) {
        Long id = (long) (int) tagInfo.get("id");
        long profileId = (int) tagInfo.get("profileId");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                //연관 프로필 탐색
                boolean followTagExist = profileRepository.followTagExist(id);

                //연관 포스트 탐색
                boolean relationTagExist = postRepository.relationTagExist(id);

                if(!followTagExist && !relationTagExist) {
                    Optional<Tag> optionalTag = tagRepository.findById(id);

                    optionalTag.ifPresentOrElse(tag -> {
                        if(tag.getTagImgUrl() != null) {
                            String tagImgUrl = tag.getTagImgUrl();

                            Map<String ,Object> imgUrlInfo = new HashMap<>();

                            imgUrlInfo.put("imgUrl", tagImgUrl);

                            resourceService.delete(imgUrlInfo);
                        }

                        tagRepository.delete(tag);
                    }, () -> {
                        throw new TagNotFoundException("Tag Not Found.");
                    });
                } else {
                    throw new TagRelationException("There are data related to tag.");
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

    public List<TagDto> searchFollowerRank() {
        return tagRepository.searchFollowerRank();
    }

    public List<TagDto> searchPostRank() {
        return tagRepository.searchPostRank();
    }

    public TagDetailDto searchTagDetail(String name) throws UnsupportedEncodingException {
        String tagName = name;

        if(specialWordExist(name)) {
            tagName = URLDecoder.decode(name, "UTF-8");
        }

        Optional<Tag> optionalTag = Optional.ofNullable(tagRepository.findByName(tagName));

        Tag tag = optionalTag.orElseThrow(() -> {
            throw new TagNotFoundException("Tag Not Found");
        });

        return tagRepository.searchTagDetail(tag.getId());
    }

    private boolean specialWordExist(String target) {
        boolean exist = false;

        String[] checkWords = {"%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2A", "%2B",
                "%2C", "%2D", "%2E", "%2F", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F", "%40", "%5B", "%5C", "%5D",
                "%5E", "%5F", "%60", "%7B", "%7C", "%7D", "%7E"};

        for (String checkWord : checkWords) {
            if(target.contains(checkWord)) {
                exist = true;
                break;
            }
        }

        return exist;
    }
}
