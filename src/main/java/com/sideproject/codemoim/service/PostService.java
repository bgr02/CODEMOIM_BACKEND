package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.FollowRelationDto;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.dto.PostInfoDto;
import com.sideproject.codemoim.dto.PostWithCommentDto;
import com.sideproject.codemoim.exception.*;
import com.sideproject.codemoim.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final ProfileRepository profileRepository;
    private final TagRepository tagRepository;
    private final PostVoteRepository postVoteRepository;
    private final ResourceService resourceService;
    private final ProfileService profileService;
    private final PostTagRepository postTagRepository;
    private final TagService tagService;
    private final NotificationRepository notificationRepository;

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public void modifyViewCount(Map<String, Object> postInfo) {
        long id = (int) postInfo.get("id");

        Post post = postRepository.searchPostByIdAndStatus(id).orElseThrow(() -> {
            throw new PostNotFoundException("Post Not Found");
        });

        post.updateViewCount(1);
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public Long createPost(Map<String, Object> postInfo, Long userId) {
        long boardId = (int) postInfo.get("boardId");
        long profileId = (int) postInfo.get("profileId");
        String title = (String) postInfo.get("title");
        Map<String, List<String>> tagNames = (Map<String, List<String>>) postInfo.get("tagNames");
        String content = (String) postInfo.get("content");
        final Long[] postId = new Long[1];

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Board board = boardRepository.searchByIdAndStatus(boardId).orElseThrow(() -> {
                    throw new BoardNotFoundException("Board Not Found");
                });

                List<Role> roles = profile.getUser().getRoles();
                boolean authorityFlag = false;

                for (Role role : roles) {
                    RoleName roleName = role.getName();
                    String name = roleName.name();

                    if(name.contains(board.getAuthority()) || RoleName.ROLE_ADMIN.name().contains(name)) {
                        authorityFlag = true;
                        break;
                    }
                }

                if(authorityFlag) {
                    Post post = Post.builder()
                            .board(board)
                            .profile(profile)
                            .title(title)
                            .content(content)
                            .status(true)
                            .viewCount(0)
                            .totalThumbsupVoteCount(0)
                            .totalThumbsdownVoteCount(0)
                            .createdDate(LocalDateTime.now())
                            .updatedDate(LocalDateTime.now())
                            .build();

                    Post savePost = postRepository.save(post);

                    Set<Tag> tagList = new LinkedHashSet<>();

                    if (tagNames.containsKey("existTags")) {
                        List<String> existTags = tagNames.get("existTags");

                        if (!existTags.isEmpty()) {
                            for (String existTag : existTags) {
                                Tag tag = tagRepository.findByName(existTag);

                                tag.updatePostTagCount(1);

                                tagList.add(tag);
                            }
                        }
                    }

                    if (tagNames.containsKey("newTags")) {
                        List<String> newTags = tagNames.get("newTags");

                        if (!newTags.isEmpty()) {
                            for (String newTag : newTags) {
                                boolean nameDuplicateFlag = tagRepository.duplicateCheckName(newTag);

                                if (!nameDuplicateFlag) {
                                    Tag tag = Tag.builder()
                                            .profileFollowerCount(0)
                                            .postTagCount(1)
                                            .name(newTag)
                                            .build();

                                    Tag saveTag = tagRepository.save(tag);

                                    tagList.add(saveTag);
                                } else {
                                    Tag tag = tagRepository.findByName(newTag);

                                    tag.updatePostTagCount(1);

                                    tagList.add(tag);
                                }
                            }
                        }
                    }

                    Set<Tag> sortTagList = new LinkedHashSet<>();

                    if (!tagList.isEmpty()) {
                        List<String> sortTags = tagNames.get("sortTags");

                        for (String sortTag : sortTags) {
                            for (Tag tag : tagList) {
                                if (sortTag.equals(tag.getName())) {
                                    sortTagList.add(tag);

                                    break;
                                }
                            }
                        }
                    }

                    if (!sortTagList.isEmpty()) {
                        for (Tag tag : sortTagList) {
                            PostTag postTag = PostTag.builder()
                                    .post(savePost)
                                    .tag(tag)
                                    .build();

                            postTagRepository.save(postTag);
                        }
                    }

                    profile.updateContributionPoint(10);

                    postId[0] = savePost.getId();
                } else {
                    postId[0] = null;
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return postId[0];
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public void modifyPost(Map<String, Object> postInfo, Long userId) {
        long boardId = (int) postInfo.get("boardId");
        Map<String, List<String>> tagNames = (Map<String, List<String>>) postInfo.get("tagNames");
        String title = (String) postInfo.get("title");
        List<String> deleteImgUrls = (List<String>) postInfo.get("deleteImgUrls");
        long postId = (int) postInfo.get("postId");
        long profileId = (int) postInfo.get("profileId");
        String content = (String) postInfo.get("content");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Post post = postRepository.searchPostByIdAndProfileIdAndStatus(postId, profileId).orElseThrow(() -> {
                    throw new PostNotFoundException("Post Not Found.");
                });

                Board board = boardRepository.searchByIdAndStatus(boardId).orElseThrow(() -> {
                    throw new BoardNotFoundException("Board Not Found.");
                });

                List<Role> roles = profile.getUser().getRoles();
                boolean authorityFlag = false;

                for (Role role : roles) {
                    RoleName roleName = role.getName();
                    String name = roleName.name();

                    if(name.contains(board.getAuthority()) || RoleName.ROLE_ADMIN.name().contains(name)) {
                        authorityFlag = true;
                        break;
                    }
                }

                if(authorityFlag) {
                    post.updateBoard(board);

                    Set<PostTag> postTags = post.getPostTags();

                    if (tagNames.containsKey("deleteTags")) {
                        List<String> deleteTags = tagNames.get("deleteTags");

                        if (!deleteTags.isEmpty()) {
                            for (String deleteTag : deleteTags) {
                                Tag tag = tagRepository.findByName(deleteTag);

                                for (PostTag postTag : postTags) {
                                    if (postTag.getTag().getId().equals(tag.getId())) {
                                        tag.updatePostTagCount(-1);

                                        postTagRepository.delete(postTag);

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Set<Tag> addTags = new LinkedHashSet<>();

                    if (tagNames.containsKey("existTags")) {
                        List<String> existTags = tagNames.get("existTags");

                        if (!existTags.isEmpty()) {
                            for (String existTag : existTags) {
                                Tag tag = tagRepository.findByName(existTag);

                                boolean existFlag = true;

                                if (tag != null) {
                                    for (PostTag postTag : postTags) {
                                        if (postTag.getTag().getId().equals(tag.getId())) {
                                            existFlag = false;

                                            break;
                                        }
                                    }
                                } else {
                                    throw new TagNotFoundException("Tag Not Found");
                                }

                                if (existFlag) {
                                    tag.updatePostTagCount(1);

                                    addTags.add(tag);
                                }
                            }
                        }
                    }

                    if (tagNames.containsKey("newTags")) {
                        List<String> newTags = tagNames.get("newTags");

                        if (!newTags.isEmpty()) {
                            for (String newTag : newTags) {
                                boolean newFlag = true;

                                for (PostTag postTag : postTags) {
                                    if (postTag.getTag().getName().equals(newTag)) {
                                        newFlag = false;
                                    }
                                }

                                if (newFlag) {
                                    boolean nameDuplicateFlag = tagRepository.duplicateCheckName(newTag);

                                    if (!nameDuplicateFlag) {
                                        Tag tag = Tag.builder()
                                                .profileFollowerCount(0)
                                                .postTagCount(1)
                                                .name(newTag)
                                                .build();

                                        Tag saveTag = tagRepository.save(tag);

                                        addTags.add(saveTag);
                                    } else {
                                        Tag tag = tagRepository.findByName(newTag);

                                        tag.updatePostTagCount(1);

                                        addTags.add(tag);
                                    }
                                }
                            }
                        }
                    }

                    if (!addTags.isEmpty()) {
                        List<String> sortTags = tagNames.get("sortTags");

                        for (String sortTag : sortTags) {
                            for (Tag tag : addTags) {
                                if (sortTag.equals(tag.getName())) {
                                    PostTag postTag = PostTag.builder()
                                            .post(post)
                                            .tag(tag)
                                            .build();

                                    postTagRepository.save(postTag);

                                    break;
                                }
                            }
                        }
                    }

                    post.updateTitle(title);

                    if (!deleteImgUrls.isEmpty()) {
                        for (String deleteImgUrl : deleteImgUrls) {
                            Map<String ,Object> imgUrlInfo = new HashMap<>();

                            imgUrlInfo.put("imgUrl", deleteImgUrl);

                            resourceService.delete(imgUrlInfo);
                        }
                    }

                    post.updateContent(content);
                    post.updateUpdatedDate(LocalDateTime.now());
                } else {
                    throw new BadCredentialsException("The user does not have authority over the board.");
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
    public void deletePost(Map<String, Object> deleteInfo, Long userId) {
        long postId = (int) deleteInfo.get("postId");
        long profileId = (int) deleteInfo.get("profileId");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Post post = postRepository.searchPostByIdAndStatus(postId).orElseThrow(() -> {
                    throw new PostNotFoundException("Post Not Found.");
                });

                int scarpCount = (int) postRepository.getScrapCount(postId);

                int point = -10 + (post.getTotalThumbsupVoteCount() * -1) + (post.getTotalThumbsdownVoteCount() * -1) + (scarpCount * -1);

                profile.updateContributionPoint(point);

                post.updateViewCount(post.getViewCount() * -1);
                post.updateTotalThumbsupVoteCount(post.getTotalThumbsupVoteCount() * -1);
                post.updateTotalThumbsdownVoteCount(post.getTotalThumbsdownVoteCount() * -1);
                post.updateStatus(false);

                List<Profile> profileList = postRepository.searchScrapUserByPostId(postId);

                if (!profileList.isEmpty()) {
                    for (Profile scrapProfile : profileList) {
                        scrapProfile.removePost(post);
                    }
                }

                Set<PostTag> postTags = post.getPostTags();

                if (!postTags.isEmpty()) {
                    for (PostTag postTag : postTags) {
                        String name = postTag.getTag().getName();

                        Tag tag = tagRepository.findByName(name);

                        if (tag != null) {
                            tag.updatePostTagCount(-1);

                            postTagRepository.delete(postTag);
                        } else {
                            throw new TagNotFoundException("Tag Not Found");
                        }
                    }
                }

                postVoteRepository.deletePostVoteByPost(post);

                notificationRepository.deleteNotificationByPostAndType(post, "post_recommend");
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

    public Page<PostDto> searchPostList(Pageable pageable, String type, Long boardId) {
        return postRepository.searchPostList(pageable, type, boardId);
    }

    public PostInfoDto searchInfoPost(Long postId, Long profileId) {
        return postRepository.searchPostInfo(postId, profileId);
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public boolean votePostProcess(Map<String, Object> voteInfo, Long userId) {
        long postId = (int) voteInfo.get("postId");
        long writerId = (int) voteInfo.get("writerId");
        long profileId = (int) voteInfo.get("profileId");
        String type = (String) voteInfo.get("type");
        int voteCount = (int) voteInfo.get("voteCount");
        final boolean[] votePostFlag = {false};

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                PostVote findPostVote = postVoteRepository.searchPostVoteByPostIdAndProfileId(postId, profileId);

                if (findPostVote != null) {
                    if(findPostVote.getVoteCount() == 0) {
                        if((voteCount > 0 && type.equals("up")) || (voteCount < 0 && type.equals("down"))) {
                            findPostVote.updateVoteCount(voteCount);

                            votePostFlag[0] = true;
                        }
                    } else if(findPostVote.getVoteCount() > 0) {
                        if(voteCount < 0 && type.equals("upCancel")) {
                            findPostVote.updateVoteCount(voteCount);

                            votePostFlag[0] = true;
                        }
                    } else if(findPostVote.getVoteCount() < 0) {
                        if(voteCount > 0 && type.equals("downCancel")) {
                            findPostVote.updateVoteCount(voteCount);

                            votePostFlag[0] = true;
                        }
                    }
                } else {
                    Post post = postRepository.searchPostByIdAndStatus(postId).orElseThrow(() -> {
                        throw new PostNotFoundException("Post Not Found.");
                    });

                    PostVote postVote = PostVote.builder()
                            .post(post)
                            .profile(profile)
                            .voteCount(voteCount)
                            .build();

                    postVoteRepository.save(postVote);

                    votePostFlag[0] = true;
                }

                if(votePostFlag[0]) {
                    if (voteCount > 0 && writerId != profileId) {
                        Profile writer = profileRepository.findById(writerId).orElseThrow(() -> {
                            throw new ProfileNotFoundException("Profile Not Found");
                        });

                        writer.updateContributionPoint(1);
                    } else if (voteCount < 0 && writerId != profileId) {
                        Profile writer = profileRepository.findById(writerId).orElseThrow(() -> {
                            throw new ProfileNotFoundException("Profile Not Found");
                        });

                        writer.updateContributionPoint(-1);
                    }
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return votePostFlag[0];
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public void voteProcess(Map<String, Object> voteInfo, Long userId) {
        long postId = (int) voteInfo.get("postId");
        long profileId = (int) voteInfo.get("profileId");
        String voteType = (String) voteInfo.get("voteType");
        int voteCount = (int) voteInfo.get("voteCount");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Post post = postRepository.searchPostByIdAndStatus(postId).orElseThrow(() -> {
                    throw new PostNotFoundException("Post Not Found");
                });

                if (voteType.equals("up")) {
                    post.updateTotalThumbsupVoteCount(voteCount);
                } else {
                    post.updateTotalThumbsdownVoteCount(voteCount);
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
    public boolean scrapPost(Map<String, Object> scrapInfo, Long userId) {
        long postId = (int) scrapInfo.get("postId");
        long writerId = (int) scrapInfo.get("writerId");
        long profileId = (int) scrapInfo.get("profileId");
        String type = (String) scrapInfo.get("type");
        final boolean[] scrapFlag = {false};

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Optional<Profile> optionalScrapProfile = Optional.ofNullable(profileRepository.searchScrapByProfileAndPost(profileId, postId));

                Post post = postRepository.searchPostByIdAndStatus(postId).orElseThrow(() -> {
                    throw new PostNotFoundException("Post Not Found.");
                });

                optionalScrapProfile.ifPresentOrElse(scrapProfile -> {
                    scrapProfile.removePost(post);

                    if (writerId != profileId) {
                        Profile writer = profileRepository.findById(writerId).orElseThrow(() -> {
                            throw new ProfileNotFoundException("Profile Not Found");
                        });

                        writer.updateContributionPoint(-1);
                    }

                    if(type.equals("cancel")) {
                        scrapFlag[0] = true;
                    }
                }, () -> {
                    profile.addPost(post);

                    if (writerId != profileId) {
                        Profile writer = profileRepository.findById(writerId).orElseThrow(() -> {
                            throw new ProfileNotFoundException("Profile Not Found");
                        });

                        writer.updateContributionPoint(1);
                    }

                    if(type.equals("scrap")) {
                        scrapFlag[0] = true;
                    }
                });
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return scrapFlag[0];
    }

    public Page<PostDto> searchTagPost(Pageable pageable, String type, String name) throws UnsupportedEncodingException {
        String tagName = name;

        if(specialWordExist(tagName)) {
            tagName = URLDecoder.decode(name, "UTF-8");
        }

        Optional<Tag> optionalTag = Optional.ofNullable(tagRepository.findByName(tagName));

        Tag tag = optionalTag.orElseThrow(() -> {
            throw new TagNotFoundException("Tag Not Found");
        });

        return postRepository.searchTagPost(pageable, type, tag.getId());
    }

    public List<PostDto> searchDashboardFixedPostList(String type) {
        return postRepository.searchDashboardFixedPostList(type);
    }

    public List<Map<String, Object>> searchDashboardNonFixedPostList() {
        return postRepository.searchDashboardNonFixedPostList();
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

    public Page<PostDto> searchPostListByProfileId(Pageable pageable, Long id) {
        return postRepository.searchPostListByProfileId(pageable, id);
    }

    public Page<PostWithCommentDto> searchCommentPostListByProfileId(Pageable pageable, Long id) {
        return postRepository.searchCommentPostListByProfileId(pageable, id);
    }

    public Page<PostDto> searchScrapListByProfileId(Pageable pageable, Long id) {
        return postRepository.searchScrapListByProfileId(pageable, id);
    }
}
