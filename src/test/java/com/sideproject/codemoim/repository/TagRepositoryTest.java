package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.TagCountDto;
import com.sideproject.codemoim.dto.TagDetailDto;
import com.sideproject.codemoim.dto.TagDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Test
    @DisplayName("태그 이름 중복체크 테스트")
    void duplicateCheckNameTest() {
        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        tagRepository.save(tag);

        Assertions.assertTrue(tagRepository.duplicateCheckName("test"));
    }

    @Test
    @DisplayName("태그 리스트 검색 테스트")
    void searchTagListTest() {
        for(int i=0;  i < 10; i++) {
            Tag tag = Tag
                    .builder()
                    .name("test" + i)
                    .profileFollowerCount(0)
                    .postTagCount(0)
                    .build();

            tagRepository.save(tag);
        }

        Pageable pageRequest = PageRequest.of(0, 5);

        Page<TagDto> tagDtos = tagRepository.searchTagList(pageRequest);

        Assertions.assertEquals(5, tagDtos.get().count());
    }

    @Test
    @DisplayName("모든 태그 조회 테스트")
    void searchTagAllListTest() {
        Tag testTag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        tagRepository.save(testTag);

        Tag testTag2 = Tag
                .builder()
                .name("test2")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        tagRepository.save(testTag2);

        List<TagDto> tagDtos = tagRepository.searchTagAllList();

        Assertions.assertEquals(tagDtos.size(), 2);
    }

    @Test
    @DisplayName("태그 정보 검색 테스트")
    void searchTagInfo() {
        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

        TagDto tagDto = tagRepository.searchInfoTag(tag.getId());

        Assertions.assertEquals(saveTag.getId(), tagDto.getId());
    }

    @Test
    @DisplayName("포스트에서 사용되고 있는 태그의 개수를 조회하는 테스트")
    void searchTagCountListTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Board board = Board.builder()
                .name("test")
                .status(true)
                .url("/test")
                .icon("test")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveBoard = boardRepository.save(board);

        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

        Post post = Post.builder()
                .board(saveBoard)
                .profile(saveProfile)
                .title("test")
                .content("test")
                .status(true)
                .viewCount(0)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Post savePost = postRepository.save(post);

        PostTag postTag = PostTag.builder()
                .post(savePost)
                .tag(saveTag)
                .build();

        postTagRepository.save(postTag);

        List<TagCountDto> tagCountDtoList = tagRepository.searchTagCountList();

        Assertions.assertEquals(tagCountDtoList.get(0).getCount(), 1);
        Assertions.assertEquals(tagCountDtoList.get(0).getName(), saveTag.getName());
    }

    @Test
    @DisplayName("팔로우 태그 순위 조회 테스트")
    void searchFollowerRankTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

        Set<Tag> tags = new LinkedHashSet<>();
        tags.add(saveTag);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .tags(tags)
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        List<TagDto> tagDtoList = tagRepository.searchFollowerRank();

        Assertions.assertEquals(saveTag.getId(), tagDtoList.get(0).getId());
    }

    @Test
    @DisplayName("포스트 사용 태그 순위 조회 테스트")
    void searchPostRankTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Board board = Board.builder()
                .name("test")
                .status(true)
                .url("/test")
                .icon("test")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveBoard = boardRepository.save(board);

        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

        Post post = Post.builder()
                .board(saveBoard)
                .profile(saveProfile)
                .title("test")
                .content("test")
                .status(true)
                .viewCount(0)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Post savePost = postRepository.save(post);

        PostTag postTag = PostTag.builder()
                .post(savePost)
                .tag(saveTag)
                .build();

        postTagRepository.save(postTag);

        List<TagDto> tagDtoList = tagRepository.searchPostRank();

        Assertions.assertEquals(saveTag.getId(), tagDtoList.get(0).getId());
    }

    @Test
    @DisplayName("태그 상세정보 조회 테스트")
    void searchTagDetailTest() {
        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Set<Tag> profileTags = new LinkedHashSet<>();
        profileTags.add(saveTag);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .tags(profileTags)
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        saveTag.updateProfileFollowerCount(1);

        Board board = Board.builder()
                .name("test")
                .status(true)
                .url("/test")
                .icon("test")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveBoard = boardRepository.save(board);

        Post post = Post.builder()
                .board(saveBoard)
                .profile(saveProfile)
                .title("test")
                .content("test")
                .status(true)
                .viewCount(0)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Post savePost = postRepository.save(post);

        PostTag postTag = PostTag.builder()
                .post(savePost)
                .tag(saveTag)
                .build();

        postTagRepository.save(postTag);

        saveTag.updatePostTagCount(1);

        TagDetailDto tagDetailDto = tagRepository.searchTagDetail(saveTag.getId());

        Assertions.assertEquals(saveTag.getId(), tagDetailDto.getId());
        Assertions.assertEquals(1, tagDetailDto.getFollowerCount());
        Assertions.assertEquals(1, tagDetailDto.getPostCount());
    }

}