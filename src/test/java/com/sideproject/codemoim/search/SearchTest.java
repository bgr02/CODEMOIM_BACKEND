package com.sideproject.codemoim.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.repository.*;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SearchTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    CustomSearchRepository customSearchRepository;

    Board saveBoard;

    @BeforeEach
    void createPost() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        List<Role> roles = new ArrayList<>();
        roles.add(saveRole);

        User user = User.builder()
                .username("tester")
                .password(passwordEncoder.encode("1234"))
                .status((byte) 0)
                .roles(roles)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("test")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Board board = Board.builder()
                .name("test")
                .status(true)
                .url("/test")
                .icon("test")
                .sort(0)
                .display(true)
                .authority("USER")
                .build();

        saveBoard = boardRepository.save(board);

        Tag tag = Tag
                .builder()
                .name("Java")
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
    }

    @Test
    @DisplayName("검색어를 통한 포스트 조회 테스트 - Hibernate Search 사용")
    void searchPostByKeywordTest() throws Exception {
        //기능 테스트
        Map<String, Object> pageMap = new HashMap<>();

        pageMap.put("page", 0L);
        pageMap.put("size", 10);

        mockMvc
                .perform(get("/search/post-keyword")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(pageMap))
                        .param("keyword", "test")
                )
                .andExpect(status().isOk());

        //성능 테스트
        /*long initializeStartTime = System.currentTimeMillis();

        customSearchRepository.buildSearchIndex();

        long initializeEndTime = System.currentTimeMillis() - initializeStartTime;

        System.out.println("Hibernate Search 초기화 시간: " + initializeEndTime + " ms");

        long searchStartTime = System.currentTimeMillis();

        Page<PostDto> postDtos = customSearchRepository.searchPostByKeyword(PageRequest.of(0, 10), "테스트");

        Assertions.assertNotEquals(0, postDtos.getTotalElements());

        long searchEndTime = System.currentTimeMillis() - searchStartTime;

        System.out.println("Hibernate Search 검색 소요시간: " + searchEndTime + " ms");*/
    }

    @Test
    @DisplayName("검색어와 게시판 아이디를 통한 포스트 조회 테스트 - Hibernate Search 사용")
    void searchPostByKeywordAndBoardIdTest() throws Exception {
        Map<String, Object> pageMap = new HashMap<>();

        pageMap.put("page", 0L);
        pageMap.put("size", 10);

        mockMvc
                .perform(get("/search/post-keyword-boardId")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(pageMap))
                        .param("keyword", "test")
                        .param("boardId", String.valueOf(saveBoard.getId()))
                )
                .andExpect(status().isOk());

//        Page<PostDto> postDtos = customSearchRepository.searchPostByKeywordAndBoardId(PageRequest.of(0, 10), "test", 1L);
//
//        Assertions.assertNotEquals(0, postDtos.getTotalElements());
    }

    @Test
    @DisplayName("검색어와 태그 이름을 통한 포스트 조회 테스트 - Hibernate Search 사용")
    void searchPostByKeywordAndTagNameTest() throws Exception {
        Map<String, Object> pageMap = new HashMap<>();

        pageMap.put("page", 0L);
        pageMap.put("size", 10);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("keyword", "test");
        params.add("tagName", "Java");

        mockMvc
                .perform(get("/search/post-keyword-tagName")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(pageMap))
                        .params(params)
                )
                .andExpect(status().isOk());

//        Page<PostDto> postDtos = customSearchRepository.searchPostByKeywordAndTagName(PageRequest.of(0, 10), "test", "Java");
//
//        Assertions.assertNotEquals(0, postDtos.getTotalElements());
    }

    @Test
    @DisplayName("검색어를 통한 포스트 조회 테스트 - MySQL like 문법 사용")
    void searchPostByKeywordUseLikeTest() throws Exception {
        long startTime = System.currentTimeMillis();

        //기능 테스트
        Page<PostDto> postDtos = customSearchRepository.searchPostByKeywordUseLike(PageRequest.of(0, 10), "test");

        //성능 테스트
        //Page<PostDto> postDtos = customSearchRepository.searchPostByKeywordUseLike(PageRequest.of(0, 10), "테스트");

        Assertions.assertNotEquals(0L, postDtos.getTotalElements());

        long endTime = System.currentTimeMillis() - startTime;

        System.out.println("MySQL like 검색 소요시간: " + endTime + " ms");
    }

}
