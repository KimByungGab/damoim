package com.sideproject.damoim.service;

import com.sideproject.damoim.advice.exception.*;
import com.sideproject.damoim.dto.sProject.volunteer.SProjectVolunteerListResponse;
import com.sideproject.damoim.entity.SProjectRecruitGroup;
import com.sideproject.damoim.entity.SProjectVolunteer;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.repository.SProjectRecruitGroupRepository;
import com.sideproject.damoim.repository.SProjectVolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SProjectVolunteerService {

    private final String ACCEPT = "A";
    private final String DECLINE = "D";
    private final String BEFORE_DECISION = "B";

    private final SProjectVolunteerRepository sProjectVolunteerRepository;
    private final SProjectRecruitGroupRepository sProjectRecruitGroupRepository;

    // 게시글의 지원자 리스트 조회
    public List<SProjectVolunteerListResponse> getCurrentRecruitStatus(Long postIdx) {

        List<SProjectVolunteerListResponse> responses = new ArrayList<>();

        List<SProjectRecruitGroup> groupInfo = sProjectRecruitGroupRepository.findBypost_Idx(postIdx).orElseThrow(CContentNotFoundException::new);

        for(SProjectRecruitGroup group : groupInfo) {

            SProjectVolunteerListResponse response = new SProjectVolunteerListResponse();
            int totalVolunteerCount = sProjectVolunteerRepository.countBysProjectRecruitGroup_Idx(group.getIdx());

            SProjectVolunteerListResponse groupResponse = new SProjectVolunteerListResponse();
            groupResponse.setIdx(group.getIdx());
            groupResponse.setName(group.getName());
            groupResponse.setTotalCount(group.getRecruitCount());
            groupResponse.setCurrentCount(totalVolunteerCount);

            responses.add(groupResponse);
        }

        return responses;
    }

    // 게시글의 지원 그룹에 지원
    public void addVolunteer(User user, Long groupIdx) {
        int existCount = sProjectVolunteerRepository.countBysProjectRecruitGroup_IdxAndUser_Idx(groupIdx, user.getIdx());

        if(existCount != 0)
            throw new CAlreadyExistContentException();

        SProjectRecruitGroup group = sProjectRecruitGroupRepository.findById(groupIdx).orElseThrow(CContentNotFoundException::new);

        if(group.getPost().getUser().getIdx().equals(user.getIdx()))
            throw new CCanNotProcessYourOwnException();

        SProjectVolunteer newVolunteer = SProjectVolunteer.builder()
                .sProjectRecruitGroup(group)
                .user(user)
                .result(BEFORE_DECISION)
                .createdAt(LocalDateTime.now())
                .build();

        sProjectVolunteerRepository.save(newVolunteer);
    }

    // 지원 취소
    public void cancelApplicationVolunteerGroup(User user, Long groupIdx) {
        SProjectVolunteer myApplication = sProjectVolunteerRepository.findBysProjectRecruitGroup_IdxAndUser_Idx(groupIdx, user.getIdx())
                .orElseThrow(CContentNotFoundException::new);

        if(!myApplication.getResult().equals(BEFORE_DECISION))
            throw new CAlreadyProcessedContentException();

        sProjectVolunteerRepository.delete(myApplication);
    }

    // 지원 승낙
    public void acceptVolunteer(User recruiter, Long volunteerIdx) {
        SProjectVolunteer currentVolunteer = sProjectVolunteerRepository.findById(volunteerIdx)
                .orElseThrow(CContentNotFoundException::new);

        if(!currentVolunteer.getSProjectRecruitGroup().getPost().getUser().getIdx().equals(recruiter.getIdx()))
            throw new CAccessDeniedException();

        if(!currentVolunteer.getResult().equals(BEFORE_DECISION))
            throw new CAlreadyProcessedContentException();

        currentVolunteer.setResult(ACCEPT);
        sProjectVolunteerRepository.save(currentVolunteer);
    }

    // 지원 거절
    public void declineVolunteer(User recruiter, Long volunteerIdx) {
        SProjectVolunteer currentVolunteer = sProjectVolunteerRepository.findById(volunteerIdx)
                .orElseThrow(CContentNotFoundException::new);

        if(!currentVolunteer.getSProjectRecruitGroup().getPost().getUser().getIdx().equals(recruiter.getIdx()))
            throw new CAccessDeniedException();

        if(!currentVolunteer.getResult().equals(BEFORE_DECISION))
            throw new CAlreadyProcessedContentException();

        currentVolunteer.setResult(DECLINE);
        sProjectVolunteerRepository.save(currentVolunteer);
    }
}
