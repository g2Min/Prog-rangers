package com.prograngers.backend.controller;

import com.prograngers.backend.controller.auth.LoggedInMember;
import com.prograngers.backend.controller.auth.Login;
import com.prograngers.backend.dto.follow.response.ShowFollowListResponse;
import com.prograngers.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FollowController {

    private final FollowService followService;

    @Login
    @PostMapping("/members/{memberId}/following")
    public ResponseEntity<Void> follow(@LoggedInMember Long followerId,
                                       @PathVariable("memberId") Long followingId) {
        followService.follow(followerId, followingId);
        return ResponseEntity.noContent().build();
    }

    @Login
    @DeleteMapping("/members/{memberId}/following")
    public ResponseEntity<Void> unfollow(@LoggedInMember Long followerId,
                                         @PathVariable("memberId") Long followingId) {
        followService.unfollow(followerId, followingId);
        return ResponseEntity.noContent().build();
    }

    @Login
    @GetMapping("mypage/follows")
    public ResponseEntity<ShowFollowListResponse> followList(@LoggedInMember Long memberId){
        return ResponseEntity.ok().body(followService.getFollowList(memberId));
    }
}
