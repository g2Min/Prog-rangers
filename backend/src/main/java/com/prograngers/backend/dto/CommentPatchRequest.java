package com.prograngers.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentPatchRequest {
    @NotBlank(message = "댓글 내용이 비었습니다")
    private String content;
    private String mention;
}
