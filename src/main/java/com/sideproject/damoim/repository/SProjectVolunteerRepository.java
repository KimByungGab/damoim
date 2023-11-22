package com.sideproject.damoim.repository;

import com.sideproject.damoim.entity.SProjectVolunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SProjectVolunteerRepository extends JpaRepository<SProjectVolunteer, Long> {
    Optional<List<SProjectVolunteer>> findAllBysProjectRecruitGroup_Idx(Long groupIdx);
    int countBysProjectRecruitGroup_Idx(Long groupIdx);
    int countBysProjectRecruitGroup_IdxAndUser_Idx(Long groupIdx, Long userIdx);
    Optional<SProjectVolunteer> findBysProjectRecruitGroup_IdxAndUser_Idx(Long groupIdx, Long userIdx);
    Optional<SProjectVolunteer> findByuser_Idx(Long userIdx);
}
