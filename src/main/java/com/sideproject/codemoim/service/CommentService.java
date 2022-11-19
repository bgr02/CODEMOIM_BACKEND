package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.CommentNotFoundException;
import com.sideproject.codemoim.exception.PostNotFoundException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final NotificationRepository notificationRepository;

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public Long createComment(Map<String, Object> commentInfo, Long userId) {
        long profileId = (int) commentInfo.get("profileId");
        long postId = (int) commentInfo.get("postId");
        String content = (String) commentInfo.get("content");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        final Long[] commentId = new Long[1];

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                profile.updateContributionPoint(5);

                Post post = postRepository.searchPostByIdAndStatus(postId).orElseThrow(() -> {
                    throw new PostNotFoundException("Post Not Found");
                });

                Comment comment = Comment.builder()
                        .profile(profile)
                        .post(post)
                        .content(content)
                        .selectedComment(false)
                        .totalThumbsupVoteCount(0)
                        .totalThumbsdownVoteCount(0)
                        .build();

                Comment saveComment = commentRepository.save(comment);

                commentId[0] = saveComment.getId();
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return commentId[0];
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public void modifyComment(Map<String, Object> commentInfo, Long userId) {
        long profileId = (int) commentInfo.get("profileId");
        long commentId = (int) commentInfo.get("commentId");
        String content = (String) commentInfo.get("content");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
                    throw new CommentNotFoundException("Comment Not Found");
                });

                comment.updateCotent(content);
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public Map<String, Object> deleteComment(Map<String, Object> commentInfo, Long userId) {
        long id = (int) commentInfo.get("id");
        long profileId = (int) commentInfo.get("profileId");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        Map<String, Object> params = new HashMap<>();

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Optional<Comment> optionalComment = Optional.ofNullable(commentRepository.searchCommentByCommentId(id));

                Comment comment = optionalComment.orElseThrow(() -> {
                    throw new CommentNotFoundException("Comment Not Found");
                });

                Long commentProfileId = comment.getProfile().getId();
                int point = -5 + (comment.getTotalThumbsupVoteCount() * -1) + (comment.getTotalThumbsdownVoteCount() * -1);

                commentVoteRepository.deleteCommentVoteByComment(comment);

                notificationRepository.deleteNotificationByCommentAndType(comment, "comment_recommend");

                Long postProfileId = comment.getPost().getProfile().getId();

                Notification notification = notificationRepository.searchNotificationByProfileIdAndCommentIdAndType(postProfileId, comment.getId(), "comment_alarm");

                if(notification != null) {
                    notificationRepository.delete(notification);

                    params.put("read", notification.getRead());
                    params.put("notificationId", notification.getId());
                }

                commentRepository.delete(comment);

                Profile commentProfile  = profileRepository.findById(commentProfileId).orElseThrow(() -> {
                    throw new ProfileNotFoundException("Profile Not Found");
                });

                commentProfile.updateContributionPoint(point);
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return params;
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public void selectComment(Map<String, Object> commentInfo, Long userId) {
        long id = (int) commentInfo.get("id");
        long profileId = (int) commentInfo.get("profileId");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        Map<String, Object> params = new HashMap<>();

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Comment comment = commentRepository.findById(id).orElseThrow(() -> {
                    throw new CommentNotFoundException("Comment Not Found.");
                });

                boolean selectedComment = comment.getSelectedComment();

                if(!selectedComment) {
                    comment.updateSelectedComment(true);
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
    public boolean voteCommentProcess(Map<String, Object> commentInfo, Long userId) {
        long commentId = (int) commentInfo.get("commentId");
        long writerId = (int) commentInfo.get("writerId");
        long profileId = (int) commentInfo.get("profileId");
        String type = (String) commentInfo.get("type");
        int voteCount = (int) commentInfo.get("voteCount");
        final boolean[] voteCommentFlag = {false};

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                CommentVote findCommentVote = commentVoteRepository.searchCommentVoteByCommentIdAndProfileId(commentId, profileId);

                if(findCommentVote != null) {
                    if(findCommentVote.getVoteCount() == 0) {
                        if((voteCount > 0 && type.equals("up")) || (voteCount < 0 && type.equals("down"))) {
                            findCommentVote.updateVoteCount(voteCount);

                            voteCommentFlag[0] = true;
                        }
                    } else if(findCommentVote.getVoteCount() > 0) {
                        if(voteCount < 0 && type.equals("upCancel")) {
                            findCommentVote.updateVoteCount(voteCount);

                            voteCommentFlag[0] = true;
                        }
                    } else if(findCommentVote.getVoteCount() < 0) {
                        if(voteCount > 0 && type.equals("downCancel")) {
                            findCommentVote.updateVoteCount(voteCount);

                            voteCommentFlag[0] = true;
                        }
                    }
                } else {
                    Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
                        throw new CommentNotFoundException("Comment Not Found.");
                    });

                    CommentVote commentVote = CommentVote.builder()
                            .profile(profile)
                            .comment(comment)
                            .voteCount(voteCount)
                            .build();

                    commentVoteRepository.save(commentVote);

                    voteCommentFlag[0] = true;
                }

                if(voteCommentFlag[0]) {
                    Profile writerProfile = profileRepository.findById(writerId).orElseThrow(() -> {
                        throw new ProfileNotFoundException("Profile Not Found");
                    });

                    if(voteCount > 0 && writerId != profileId) {
                        writerProfile.updateContributionPoint(1);
                    } else if(voteCount < 0 && writerId != profileId) {
                        writerProfile.updateContributionPoint(-1);
                    }
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        return voteCommentFlag[0];
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public void voteProcess(Map<String, Object> commentInfo, Long userId) {
        long profileId = (int) commentInfo.get("profileId");
        long commentId = (int) commentInfo.get("commentId");
        String voteType = (String) commentInfo.get("voteType");
        int voteCount = (int) commentInfo.get("voteCount");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
                    throw new CommentNotFoundException("Comment Not Found.");
                });

                if (voteType.equals("up")) {
                    comment.updateTotalThumbsupVoteCount(voteCount);
                } else {
                    comment.updateTotalThumbsdownVoteCount(voteCount);
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

}
