package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.Tag;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.dto.TagDetailDto;
import com.sideproject.codemoim.dto.TagDto;
import com.sideproject.codemoim.exception.DuplicateTagException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.exception.TagNotFoundException;
import com.sideproject.codemoim.exception.TagRelationException;
import com.sideproject.codemoim.repository.PostRepository;
import com.sideproject.codemoim.repository.ProfileRepository;
import com.sideproject.codemoim.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;

import javax.management.relation.RelationException;
import java.net.URLDecoder;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    TagService tagService;

    @Mock
    TagRepository tagRepository;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    PostRepository postRepository;

    @Test
    @DisplayName("태그 이름 중복체크 성공 테스트")
    void duplicateCheckNameTest_success() {
        given(tagRepository.duplicateCheckName(anyString())).willReturn(true);

        Assertions.assertDoesNotThrow(() -> {
            tagService.duplicateCheckName(anyString());
        });
    }

    @Test
    @DisplayName("태그 정보 조회 성공 테스트")
    void searchInfoTagTest_success() {
        given(tagRepository.searchInfoTag(anyLong())).willReturn(new TagDto());

        Assertions.assertDoesNotThrow(() -> {
            tagService.searchInfoTag(anyLong());
        });
    }

    @Test
    @DisplayName("태그 삭제 가능여부 체크 성공 테스트")
    void searchTagDeletableCheck_success() {
        given(profileRepository.followTagExist(anyLong())).willReturn(false);
        given(postRepository.relationTagExist(anyLong())).willReturn(false);

        Assertions.assertDoesNotThrow(() -> {
            tagService.searchTagDeletableCheck(anyLong());
        });
    }

    @Test
    @DisplayName("태그 저장 성공 테스트")
    void tagSaveTest_success() {
        Map<String, Object> tagInfo = new HashMap<>();

        tagInfo.put("profileId", 1);
        tagInfo.put("name", "test");
        tagInfo.put("tagUrl", "http://www.test.com");

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        given(tagRepository.duplicateCheckName(anyString())).willReturn(false);

        Assertions.assertDoesNotThrow(() -> {
            tagService.createTag(tagInfo, anyLong());
        });
    }

    @Test
    @DisplayName("태그 저장 실패 테스트")
    void tagSaveTest_fail() {
        Map<String, Object> tagInfo = new HashMap<>();

        tagInfo.put("profileId", 1);
        tagInfo.put("name", "test");
        tagInfo.put("tagUrl", "http://www.test.com");

        given(tagRepository.duplicateCheckName(anyString())).willReturn(true);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            tagService.createTag(tagInfo, anyLong());
        });

        Assertions.assertThrows(DuplicateTagException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            tagService.createTag(tagInfo, anyLong());
        });
    }

    @Test
    @DisplayName("태그 수정 성공 테스트")
    void tagModifyTest_success() {
        Map<String, Object> tagInfo = new HashMap<>();

        tagInfo.put("id", 1);
        tagInfo.put("profileId", 1);
        tagInfo.put("name", "tester2");

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Tag tag = Tag.builder()
                .name("tester")
                .build();

        given(tagRepository.findById(anyLong())).willReturn(Optional.ofNullable(tag));
        given(tagRepository.duplicateCheckName(anyString())).willReturn(false);

        Assertions.assertDoesNotThrow(() -> {
            tagService.modifyTag(tagInfo, anyLong());
        });
    }

    @Test
    @DisplayName("태그 수정 실패 테스트")
    void tagModifyTest_fail() {
        Map<String, Object> tagInfo = new HashMap<>();

        tagInfo.put("id", 1);
        tagInfo.put("profileId", 1);
        tagInfo.put("name", "tester2");

        Tag tag = Tag.builder()
                .name("tester")
                .build();

        given(tagRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));
        given(tagRepository.duplicateCheckName(anyString())).willReturn(false);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            tagService.modifyTag(tagInfo, anyLong());
        });

        Assertions.assertThrows(TagNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            tagService.modifyTag(tagInfo, anyLong());
        });

        Assertions.assertThrows(DuplicateTagException.class, () -> {
            given(tagRepository.findById(anyLong())).willReturn(Optional.ofNullable(tag));
            given(tagRepository.duplicateCheckName(anyString())).willReturn(true);
            tagService.modifyTag(tagInfo, anyLong());
        });
    }

    @Test
    @DisplayName("태그 삭제 성공 테스트")
    void tagDeleteTest_success() {
        Map<String, Object> tagInfo = new HashMap<>();

        tagInfo.put("id", 1);
        tagInfo.put("profileId", 1);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        given(profileRepository.followTagExist(anyLong())).willReturn(false);
        given(postRepository.relationTagExist(anyLong())).willReturn(false);

        Tag tag = Tag.builder().build();
        given(tagRepository.findById(anyLong())).willReturn(Optional.ofNullable(tag));

        Assertions.assertDoesNotThrow(() -> {
            tagService.deleteTag(tagInfo, anyLong());
        });
    }

    @Test
    @DisplayName("태그 삭제 실패 테스트")
    void tagDeleteTest_fail() {
        Map<String, Object> tagInfo = new HashMap<>();

        tagInfo.put("id", 1);
        tagInfo.put("profileId", 1);

        given(profileRepository.followTagExist(anyLong())).willReturn(true);
        given(postRepository.relationTagExist(anyLong())).willReturn(true);

        Tag tag = Tag.builder().build();
        given(tagRepository.findById(anyLong())).willReturn(Optional.ofNullable(tag));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            tagService.deleteTag(tagInfo,anyLong());
        });

        Assertions.assertThrows(TagRelationException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            tagService.deleteTag(tagInfo,anyLong());
        });

        Assertions.assertThrows(TagNotFoundException.class, () -> {
            given(profileRepository.followTagExist(anyLong())).willReturn(false);
            given(postRepository.relationTagExist(anyLong())).willReturn(false);
            given(tagRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));
            tagService.deleteTag(tagInfo,anyLong());
        });
    }

    @Test
    @DisplayName("팔로우 태그 순위 리스트 조회 성공 테스트")
    void searchFollowerRankTest_success() {
        List<TagDto> tagDtoList = new ArrayList<>();

        given(tagRepository.searchFollowerRank()).willReturn(tagDtoList);

        Assertions.assertDoesNotThrow(() -> {
            tagService.searchFollowerRank();
        });
    }

    @Test
    @DisplayName("태그 포스트 순위 리스트 조회 성공 테스트")
    void searchPostRankTest_success() {
        List<TagDto> tagDtoList = new ArrayList<>();

        given(tagRepository.searchPostRank()).willReturn(tagDtoList);

        Assertions.assertDoesNotThrow(() -> {
            tagService.searchPostRank();
        });
    }

    @Test
    @DisplayName("태그 포스트 순위 리스트 조회 성공 테스트")
    void searchTagDetailTest_success() {
        Tag tag = mock(Tag.class);

        TagDetailDto tagDetailDto = new TagDetailDto();

        given(tagRepository.findByName(anyString())).willReturn(tag);
        given(tagRepository.searchTagDetail(anyLong())).willReturn(tagDetailDto);

        Assertions.assertDoesNotThrow(() -> {
            tagService.searchTagDetail("test");
        });
    }

    @Test
    @DisplayName("태그 포스트 순위 리스트 조회 실패 테스트")
    void searchTagDetailTest_fail() {
        Assertions.assertThrows(TagNotFoundException.class, () -> {
            tagService.searchTagDetail("test");
        });
    }

}