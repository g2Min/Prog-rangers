package com.prograngers.backend.service;

import com.prograngers.backend.dto.ScarpSolutionRequest;
import com.prograngers.backend.dto.SolutionPatchRequest;
import com.prograngers.backend.entity.Algorithm;
import com.prograngers.backend.entity.DataStructure;
import com.prograngers.backend.entity.Member;
import com.prograngers.backend.entity.Solution;
import com.prograngers.backend.exception.notfound.SolutionNotFoundException;
import com.prograngers.backend.repository.SolutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SolutionService {

    private final SolutionRepository solutionRepository;

    public Solution  save(Solution solution){
        Solution saved = solutionRepository.save(solution);
        return saved;
    }

//    public Solution update(Solution solution){
//            Solution updated = solutionRepository.save(solution);
//            return updated;
//    }

    public Solution update(Long solutionId, SolutionPatchRequest request) throws SolutionNotFoundException {
        Solution target = solutionRepository.findById(solutionId).orElseThrow(() -> new SolutionNotFoundException());
        Solution solution = request.toEntity(target);
        Solution updated = solutionRepository.save(solution);
        return updated;
    }

//    public void delete(Solution solution){
//            solutionRepository.delete(solution);
//    }

        public void delete(Long solutionId) throws SolutionNotFoundException {
            Solution target = solutionRepository.findById(solutionId).orElseThrow(() -> new SolutionNotFoundException());
            solutionRepository.delete(target);
    }

    public List<Solution> index(Member member){

        List<Solution> solutionList = solutionRepository.findAllByMember(member);

        return solutionList;
    }

    public Optional<Solution> findById(Long solutionId) {
        return solutionRepository.findById(solutionId);
    }

    public Solution saveScrap(Long id, ScarpSolutionRequest request) {
        Solution scrap = solutionRepository.findById(id).orElseThrow(() -> new SolutionNotFoundException());

        // 스크랩 Solution과 사용자가 폼에 입력한 내용을 토대로 새로운 Solution을 만든다
        Solution solution = Solution.builder()
                .level(request.getLevel())
                .description(request.getDescription())
                .title(request.getTitle())
                // 위 내용까지 스크랩 한 사용자가 수정할 수 있는 내용
                .isPublic(true) //스크랩한 풀이이기 때문에 무조건 공개한다
                .problem(scrap.getProblem())
                .date(LocalDate.now())
                .member(null) //로그인정보로 member를 알도록 수정해야함
                .code(scrap.getCode())
                .scraps(0)
                .scrapId(scrap)
                .algorithm(new Algorithm(null, scrap.getAlgorithm().getName()))
                .dataStructure(new DataStructure(null,scrap.getDataStructure().getName()))
                .build();

        Solution saved = solutionRepository.save(solution);

        return saved;

    }
}
