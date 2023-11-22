package com.sideproject.damoim.repository;

import com.sideproject.damoim.entity.SProjectPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SProjectPostRepository extends JpaRepository<SProjectPost, Long> {

    @Query("SELECT p FROM SProjectPost p WHERE title LIKE %:searchKeyword% OR content LIKE %:searchKeyword%")
    Page<SProjectPost> searchPosts(@Param("searchKeyword") String searchKeyword, Pageable pageable);

    Optional<SProjectPost> findByIdx(Long idx);

    Page<SProjectPost> findByuser_idx(Long userIdx, Pageable pageable);
}
