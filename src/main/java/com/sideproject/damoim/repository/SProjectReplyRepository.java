package com.sideproject.damoim.repository;

import com.sideproject.damoim.entity.SProjectPost;
import com.sideproject.damoim.entity.SProjectReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SProjectReplyRepository extends JpaRepository<SProjectReply, Long> {
    Optional<SProjectReply> findByIdx(Long replyIdx);
    Page<SProjectReply> findBysProjectPost_IdxAndParentReplyIdx(Long postIdx, Long parentReplyIdx, Pageable pageable);
    Optional<SProjectReply> findByuser_Idx(Long userIdx);
}
