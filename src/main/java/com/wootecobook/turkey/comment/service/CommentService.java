package com.wootecobook.turkey.comment.service;

import com.wootecobook.turkey.comment.domain.Comment;
import com.wootecobook.turkey.comment.domain.CommentRepository;
import com.wootecobook.turkey.comment.service.dto.CommentCreate;
import com.wootecobook.turkey.comment.service.dto.CommentResponse;
import com.wootecobook.turkey.comment.service.dto.CommentUpdate;
import com.wootecobook.turkey.comment.service.exception.CommentSaveException;
import com.wootecobook.turkey.commons.GoodResponse;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.post.service.PostService;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class CommentService {

    private static final String NOT_FOUND_MESSAGE = "해당 댓글을 찾을 수 없습니다.";

    private final PostService postService;
    private final UserService userService;
    private final CommentGoodService commentGoodService;
    private final CommentRepository commentRepository;

    public CommentService(final PostService postService, final UserService userService,
                          final CommentGoodService commentGoodService, final CommentRepository commentRepository) {
        this.postService = postService;
        this.userService = userService;
        this.commentGoodService = commentGoodService;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public Comment findById(final Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> findCommentResponsesByPostId(final Long postId, final Pageable pageable) {
        return commentRepository.findAllByPostIdAndParentIdIsNull(postId, pageable)
                .map(CommentResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> findCommentResponsesByParentId(final Long parentId, final Pageable pageable) {
        return commentRepository.findAllByParentId(parentId, pageable)
                .map(CommentResponse::from);
    }

    public CommentResponse save(final CommentCreate commentCreate, final Long userId, final Long postId) {
        final User user = userService.findById(userId);
        final Post post = postService.findById(postId);
        final Comment parent = findParentIfExist(commentCreate.getParentId());
        final Comment comment = commentCreate.toEntity(user, post, parent);

        try {
            return CommentResponse.from(commentRepository.save(comment));
        } catch (RuntimeException e) {
            throw new CommentSaveException(e.getMessage());
        }
    }

    private Comment findParentIfExist(final Long parentId) {
        return parentId == null ? null : findById(parentId);
    }

    public CommentResponse update(final CommentUpdate commentUpdate, final Long id, final Long userId) {
        Comment comment = findById(id);
        comment.isWrittenBy(userId);
        comment.update(commentUpdate.toEntity());
        return CommentResponse.from(comment);
    }

    public void delete(final Long id, final Long userId) {
        Comment comment = findById(id);
        comment.isWrittenBy(userId);
        comment.delete();
    }

    public int countByPost(final Post post) {
        return commentRepository.countByPost(post);
    }

    public GoodResponse good(final Long id, final Long userId) {
        Comment comment = findById(id);
        User user = userService.findById(userId);

        return GoodResponse.of(commentGoodService.good(comment, user), user);
    }

    public GoodResponse countGoodResponseByComment(final Long commentId, final Long userId) {
        Comment comment = findById(commentId);
        User user = userService.findById(userId);

        return GoodResponse.of(commentGoodService.findByComment(comment), user);
    }

}
