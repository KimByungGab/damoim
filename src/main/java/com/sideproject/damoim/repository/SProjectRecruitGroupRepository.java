package com.sideproject.damoim.repository;

import com.sideproject.damoim.entity.SProjectRecruitGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SProjectRecruitGroupRepository extends JpaRepository<SProjectRecruitGroup, Long> {
    Optional<List<SProjectRecruitGroup>> findBypost_Idx(Long postIdx);
}
