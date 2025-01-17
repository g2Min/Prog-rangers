package com.prograngers.backend.service;

import com.prograngers.backend.dto.follow.response.ShowFollowListResponse;
import com.prograngers.backend.entity.Follow;
import com.prograngers.backend.entity.member.Member;
import com.prograngers.backend.entity.problem.Problem;
import com.prograngers.backend.entity.solution.Solution;
import com.prograngers.backend.exception.badrequest.AlreadyFollowingException;
import com.prograngers.backend.exception.notfound.FollowNotFoundException;
import com.prograngers.backend.exception.notfound.MemberNotFoundException;
import com.prograngers.backend.repository.follow.FollowRepository;
import com.prograngers.backend.repository.member.MemberRepository;
import com.prograngers.backend.repository.solution.SolutionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.prograngers.backend.entity.solution.LanguageConstant.JAVA;
import static com.prograngers.backend.support.fixture.MemberFixture.장지담;
import static com.prograngers.backend.support.fixture.ProblemFixture.백준_문제;
import static com.prograngers.backend.support.fixture.SolutionFixture.공개_풀이;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private SolutionRepository solutionRepository;
    @InjectMocks
    private FollowService followService;

    @Test
    @DisplayName("팔로우와 팔로워가 주어지면 팔로우를 할 수 있다.")
    void 팔로우_하기() {
        Long followerId = 1L;
        Long followingId = 2L;
        Follow follow = Follow.builder().followerId(followerId).followingId(followingId).build();

        given(memberRepository.existsById(followerId)).willReturn(true);
        given(memberRepository.existsById(followingId)).willReturn(true);
        given(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).willReturn(false);
        given(followRepository.save(follow)).willReturn(follow);

        followService.follow(followerId, followingId);

        assertAll(
                () -> verify(memberRepository).existsById(followerId),
                () -> verify(memberRepository).existsById(followingId),
                () -> verify(followRepository).existsByFollowerIdAndFollowingId(followerId, followingId),
                () -> verify(followRepository).save(follow)
        );
    }

    @Test
    @DisplayName("팔로우를 끊을 수 있다.")
    void 팔로우_끊기() {
        Long followerId = 1L;
        Long followingId = 2L;
        Follow follow = Follow.builder().followerId(followerId).followingId(followingId).build();

        given(memberRepository.existsById(followerId)).willReturn(true);
        given(memberRepository.existsById(followingId)).willReturn(true);
        given(followRepository.findByFollowerIdAndFollowingId(followerId, followingId)).willReturn(Optional.of(follow));
        willDoNothing().given(followRepository).delete(follow);

        followService.unfollow(followerId, followingId);

        assertAll(
                () -> verify(memberRepository).existsById(followerId),
                () -> verify(memberRepository).existsById(followingId),
                () -> verify(followRepository).findByFollowerIdAndFollowingId(followerId, followingId),
                () -> verify(followRepository).delete(follow)
        );
    }

    @Test
    @DisplayName("팔로워가 존재하지 않을 경우 예외가 발생한다.")
    void 팔로워가_존재_하지_않을_떄() {
        Long followerId = 1L;
        Long followingId = 2L;
        given(memberRepository.existsById(followerId)).willReturn(false);
        assertAll(
                () -> assertThatThrownBy(() -> followService.follow(followerId, followingId)).isExactlyInstanceOf(MemberNotFoundException.class),
                () -> verify(memberRepository).existsById(followerId)
        );
    }

    @Test
    @DisplayName("팔로잉이 존재하지 않을 경우 예외가 발생한다.")
    void 팔로잉이_존재하지_않을_때() {
        Long followerId = 1L;
        Long followingId = 2L;
        given(memberRepository.existsById(followerId)).willReturn(true);
        given(memberRepository.existsById(followingId)).willReturn(false);
        assertAll(
                () -> assertThatThrownBy(() -> followService.follow(followerId, followingId)).isExactlyInstanceOf(MemberNotFoundException.class),
                () -> verify(memberRepository).existsById(followerId),
                () -> verify(memberRepository).existsById(followingId)
        );
    }

    @Test
    @DisplayName("이미 팔로우 내역이 있는 회원을 팔로우 했을 때 예외가 터진다.")
    void 중복_팔로우() {
        Long followerId = 1L;
        Long followingId = 2L;

        given(memberRepository.existsById(followerId)).willReturn(true);
        given(memberRepository.existsById(followingId)).willReturn(true);
        given(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).willReturn(true);

        assertAll(
                () -> assertThatThrownBy(() -> followService.follow(followerId, followingId)).isExactlyInstanceOf(AlreadyFollowingException.class),
                () -> verify(memberRepository).existsById(followerId),
                () -> verify(memberRepository).existsById(followingId),
                () -> verify(followRepository).existsByFollowerIdAndFollowingId(followerId, followingId)
        );
    }

    @Test
    @DisplayName("팔로우 되어있지 않은 경우 팔로우를 끊으려 하면 예외가 터진다.")
    void 팔로우_내역이_없는데_언팔할_경우() {

        Long followerId = 1L;
        Long followingId = 2L;
        given(memberRepository.existsById(followerId)).willReturn(true);
        given(memberRepository.existsById(followingId)).willReturn(true);
        given(followRepository.findByFollowerIdAndFollowingId(followerId, followingId)).willReturn(Optional.empty());

        assertAll(
                () -> assertThatThrownBy(() -> followService.unfollow(followerId, followingId)).isExactlyInstanceOf(FollowNotFoundException.class),
                () -> verify(memberRepository).existsById(followerId),
                () -> verify(memberRepository).existsById(followingId),
                () -> verify(followRepository).findByFollowerIdAndFollowingId(followerId, followingId)
        );
    }

    @Test
    @DisplayName("마이페이지 팔로우 목록보기 응답을 가져올 수 있다")
    void getFollowListTest(){
        //given
        // 나를 others1,2가 팔로우
        // 내가 others3,4를 팔로우
        final Long myId = 1L;
        Member me = 장지담.아이디_지정_생성(myId);
        Member others1 = 장지담.아이디_지정_생성(2L);
        Member others2 = 장지담.아이디_지정_생성(3L);
        Member others3 = 장지담.아이디_지정_생성(4L);
        Member others4 = 장지담.아이디_지정_생성(5L);

        List<Member> following = Arrays.asList(others3, others4);
        List<Member> follower = Arrays.asList(others1, others2);

        final Long problemId = 1L;
        Problem problem = 백준_문제.아이디_지정_생성(problemId);

        final Long solutionId = 1L;
        Solution solution = 공개_풀이.아이디_지정_생성(solutionId, problem, me, LocalDateTime.now(), JAVA, 3);

        when(memberRepository.findAllByFollower(myId)).thenReturn(following);
        when(memberRepository.findAllByFollowing(myId)).thenReturn(follower);
        when(solutionRepository.findOneRecentSolutionByMemberId(myId)).thenReturn(solution);
        when(memberRepository.getLimitRecommendedMembers(problem,10L,myId)).thenReturn(Arrays.asList(others4,others3,others2));

        ShowFollowListResponse expected = ShowFollowListResponse.of(
                following,
                follower,
                Arrays.asList(others4,others3,others2)
        );

        //when
        ShowFollowListResponse result = followService.getFollowList(myId);

        //then
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("사용자가 최근에 푼 문제가 없는 경우 마이페이지 팔로우 목록보기 응답을 가져올 수 있다")
    void getFollowListTestWhenNoProblem(){
        //given
        // 나를 others1,2가 팔로우
        // 내가 others3,4를 팔로우
        final Long myId = 1L;
        Member me = 장지담.아이디_지정_생성(myId);
        Member others1 = 장지담.아이디_지정_생성(2L);
        Member others2 = 장지담.아이디_지정_생성(3L);
        Member others3 = 장지담.아이디_지정_생성(4L);
        Member others4 = 장지담.아이디_지정_생성(5L);

        List<Member> following = Arrays.asList(others3, others4);
        List<Member> follower = Arrays.asList(others1, others2);

        final Long problemId = 1L;
        Problem problem = 백준_문제.아이디_지정_생성(problemId);

        final Long solutionId = 1L;
        Solution solution = 공개_풀이.아이디_지정_생성(solutionId, problem, me, LocalDateTime.now(), JAVA, 3);

        when(memberRepository.findAllByFollower(myId)).thenReturn(following);
        when(memberRepository.findAllByFollowing(myId)).thenReturn(follower);
        when(solutionRepository.findOneRecentSolutionByMemberId(myId)).thenReturn(null);
        when(memberRepository.getLimitRecommendedMembers(null,10L,myId)).thenReturn(Arrays.asList(others4,others3,others2));

        ShowFollowListResponse expected = ShowFollowListResponse.of(
                following,
                follower,
                Arrays.asList(others4,others3,others2)
        );

        //when
        ShowFollowListResponse result = followService.getFollowList(myId);

        //then
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

}