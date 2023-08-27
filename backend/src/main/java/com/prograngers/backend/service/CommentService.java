package com.prograngers.backend.service;

import com.prograngers.backend.dto.comment.request.CommentPatchRequest;
import com.prograngers.backend.dto.comment.request.CommentRequest;
import com.prograngers.backend.entity.comment.Comment;
import com.prograngers.backend.entity.member.Member;
import com.prograngers.backend.entity.solution.Solution;
import com.prograngers.backend.exception.notfound.CommentAlreadyDeletedException;
import com.prograngers.backend.exception.notfound.CommentNotFoundException;
import com.prograngers.backend.exception.notfound.MemberNotFoundException;
import com.prograngers.backend.exception.notfound.SolutionNotFoundException;
import com.prograngers.backend.exception.unauthorization.MemberUnAuthorizedException;
import com.prograngers.backend.repository.comment.CommentRepository;
import com.prograngers.backend.repository.member.MemberRepository;
import com.prograngers.backend.repository.solution.SolutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.prograngers.backend.entity.comment.CommentStatusContant.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {
    private final SolutionRepository solutionRepository;

    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;

    public List<Comment> findBySolution(Solution solution) {
        return commentRepository.findAllBySolution(solution);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException());
    }

    // 댓글 작성
    @Transactional
    public void addComment(Long solutionId, CommentRequest commentRequest, Long memberId) {

        Solution solution = solutionRepository.findById(solutionId).orElseThrow(SolutionNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Comment comment = Comment.builder().
                member(member).
                solution(solution).
                mention(commentRequest.getMention()).
                content(commentRequest.getContent()).
                createdDate(LocalDateTime.now()).parentId(commentRequest.getParentId())
                .status(CREATED)
                .build();

        Comment saved = commentRepository.save(comment);
    }
    @Transactional
    public Long updateComment(Long commentId, CommentPatchRequest commentPatchRequest, Long memberId) {
        Comment comment = findById(commentId);
        Long targetMemberId = comment.getMember().getId();

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        if (targetMemberId!=member.getId()){
            throw new MemberUnAuthorizedException();
        }

        comment.updateContent(commentPatchRequest.getContent());
        comment.updateMention(commentPatchRequest.getMention());

        Comment saved = commentRepository.save(comment);

        // 리다이렉트 하기 위해 Solution의 Id 반환
        return saved.getSolution().getId();
    }

    @Transactional
    public void deleteComment(Long commentId,Long memberId) {
        Comment comment = findById(commentId);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Long targetCommentMemberId = comment.getMember().getId();
        if (targetCommentMemberId!=member.getId()){
            throw new MemberUnAuthorizedException();
        }
        if (comment.getStatus().equals(DELETED)){
            throw new CommentAlreadyDeletedException();
        }
        comment.deleteComment();
        commentRepository.save(comment);
    }
}
