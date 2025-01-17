package com.prograngers.backend.dto.solution.response;


import com.prograngers.backend.entity.comment.Comment;
import com.prograngers.backend.entity.comment.CommentStatusConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolutionDetailComment {
    private String photo;
    private Long id;
    private String nickname;
    private String content;
    private CommentStatusConstant status;
    private List<SolutionDetailComment> replies;

    boolean mine;

    public static SolutionDetailComment from(Comment comment, ArrayList<SolutionDetailComment> replies, boolean isMine){

        return new SolutionDetailComment(comment.getMember().getPhoto(), comment.getId(), comment.getMember().getNickname(), comment.getContent(), comment.getStatus(), replies,
               isMine);
    }

    public static SolutionDetailComment from(Comment comment, boolean isMine){
        return new SolutionDetailComment(comment.getMember().getPhoto(), comment.getId(), comment.getMember().getNickname(), comment.getContent(), comment.getStatus(), null
        ,isMine);
    }

}
