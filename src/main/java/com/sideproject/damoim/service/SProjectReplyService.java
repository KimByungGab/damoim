package com.sideproject.damoim.service;

import com.sideproject.damoim.advice.exception.CAccessDeniedException;
import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.dto.sProject.reply.SProjectReplyCreateRequest;
import com.sideproject.damoim.dto.sProject.reply.SProjectReplyListResponse;
import com.sideproject.damoim.entity.SProjectPost;
import com.sideproject.damoim.entity.SProjectReply;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.repository.SProjectPostRepository;
import com.sideproject.damoim.repository.SProjectReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SProjectReplyService {

    private final Long defaultParentReplyIdx = 0L;
    private final String defaultDeleteReplyContent = "삭제된 댓글입니다.";

    private final SProjectPostRepository sProjectPostRepository;
    private final SProjectReplyRepository sProjectReplyRepository;

    // 사이드 프로젝트 게시글 댓글 작성
    public SProjectReply createSProjectReply(User user, SProjectReplyCreateRequest request) {

        // 게시글 정보 호출
        SProjectPost sProjectPost = sProjectPostRepository.findByIdx(request.getPostIdx()).orElseThrow(CContentNotFoundException::new);

        // 부모 댓글 확인
        if(!request.getParentReplyIdx().equals(defaultParentReplyIdx))
            sProjectReplyRepository.findByIdx(request.getParentReplyIdx()).orElseThrow(CContentNotFoundException::new);

        SProjectReply newReply = SProjectReply.builder()
                .user(user)
                .sProjectPost(sProjectPost)
                .content(request.getContent())
                .parentReplyIdx(request.getParentReplyIdx())
                .createdAt(LocalDateTime.now())
                .build();

        return sProjectReplyRepository.save(newReply);
    }

    // 사이드 프로젝트 게시글 댓글 불러오기
    public List<SProjectReplyListResponse> getSProjectReplyList(Long postIdx, Long parentReplyIdx, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "idx"));
        List<SProjectReplyListResponse> responses = new ArrayList<>();

        Page<SProjectReply> pageReply = sProjectReplyRepository.findBysProjectPost_IdxAndParentReplyIdx(postIdx, parentReplyIdx, pageable);
        if(pageReply.isEmpty())
            return responses;

        List<SProjectReply> replys = pageReply.getContent();

        for(SProjectReply reply : replys) {
            SProjectReplyListResponse response = new SProjectReplyListResponse();
            SProjectReplyListResponse.UserInfo userInfo = new SProjectReplyListResponse.UserInfo();

            response.setIdx(reply.getIdx());
            response.setContent(reply.getContent());
            response.setParentReplyIdx(reply.getParentReplyIdx());
            response.setCreatedAt(reply.getCreatedAt());

            userInfo.setIdx(reply.getUser().getIdx());
            userInfo.setNickname(reply.getUser().getNickname());
            response.setUserInfo(userInfo);

            responses.add(response);
        }

        return responses;
    }

    // 댓글 수정
    public void updateSProjectReply(Long replyIdx, Long userIdx, String newContent) {
        SProjectReply reply = sProjectReplyRepository.findByIdx(replyIdx)
                .orElseThrow(CContentNotFoundException::new);

        if(!userIdx.equals(reply.getUser().getIdx()))
            throw new CAccessDeniedException();

        reply.setContent(newContent);

        sProjectReplyRepository.save(reply);
    }

    // 댓글 삭제
    public void deleteSProjectReply(Long replyIdx, Long userIdx) {
        SProjectReply reply = sProjectReplyRepository.findByIdx(replyIdx)
                .orElseThrow(CContentNotFoundException::new);

        if(!userIdx.equals(reply.getUser().getIdx()))
            throw new CAccessDeniedException();

        reply.setContent(defaultDeleteReplyContent);

        sProjectReplyRepository.save(reply);
    }
}
