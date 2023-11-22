package com.sideproject.damoim.service;

import com.sideproject.damoim.advice.exception.CAccessDeniedException;
import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.dto.sProject.post.*;
import com.sideproject.damoim.entity.SProjectPost;
import com.sideproject.damoim.entity.SProjectRecruitGroup;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.repository.SProjectPostRepository;
import com.sideproject.damoim.repository.SProjectRecruitGroupRepository;
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
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SProjectPostService {

    private final SProjectPostRepository sProjectPostRepository;
    private final SProjectRecruitGroupRepository sProjectRecruitGroupRepository;

    // 사이드 프로젝트 게시글 찾기
    public List<SProjectPostListResponse> getSProjectPostList(String searchKeyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "idx"));
        List<SProjectPostListResponse> responsePosts = new ArrayList<>();

        Page<SProjectPost> pageSProjectPost = sProjectPostRepository.searchPosts(searchKeyword, pageable);
        if(pageSProjectPost.isEmpty())
            return responsePosts;

        List<SProjectPost> posts = pageSProjectPost.getContent();

        for(SProjectPost post : posts) {
            SProjectPostListResponse responseDTO = new SProjectPostListResponse();
            responseDTO.setIdx(post.getIdx());
            responseDTO.setTitle(post.getTitle());
            responseDTO.setCreatedAt(post.getCreatedAt());
            responseDTO.setOpened(post.isOpened());

            SProjectPostListResponse.UserInfo userInfo = new SProjectPostListResponse.UserInfo();
            userInfo.setIdx(post.getUser().getIdx());
            userInfo.setEmail(post.getUser().getEmail());
            userInfo.setNickname(post.getUser().getNickname());

            responseDTO.setUserInfo(userInfo);

            responsePosts.add(responseDTO);
        }

        return responsePosts;
    }

    // 사이드 프로젝트 게시글 상세보기
    public SProjectPostInfoResponse getSProjectPostInfo(Long postIdx) {
        Optional<SProjectPost> optionalSProjectPost = sProjectPostRepository.findByIdx(postIdx);
        if(optionalSProjectPost.isEmpty())
            return null;

        SProjectPost post = optionalSProjectPost.get();

        SProjectPostInfoResponse responsePost = new SProjectPostInfoResponse();
        responsePost.setContent(post.getContent());
        responsePost.setTitle(post.getTitle());
        responsePost.setCreatedAt(post.getCreatedAt());
        responsePost.setOpened(post.isOpened());

        SProjectPostInfoResponse.UserInfo userInfo = new SProjectPostInfoResponse.UserInfo();
        userInfo.setIdx(post.getUser().getIdx());
        userInfo.setEmail(post.getUser().getEmail());
        userInfo.setNickname(post.getUser().getNickname());

        responsePost.setUserInfo(userInfo);

        return responsePost;
    }

    // 사이드 프로젝트 게시글 작성
    public SProjectPost createSProjectPost(User user, SProjectPostCreateRequest.PostInfo postInfo) {
        SProjectPost newPost = SProjectPost.builder()
                .user(user)
                .title(postInfo.getTitle().trim())
                .content(postInfo.getContent().trim())
                .isOpened(true)
                .createdAt(LocalDateTime.now())
                .build();

        return sProjectPostRepository.save(newPost);
    }

    // 사이드 프로젝트 지원자 모집 그룹 작성
    public void createSProjectRecruitGroup(SProjectPost post, List<GroupInfo> groupInfos) {
        for(GroupInfo groupInfo : groupInfos) {

            SProjectRecruitGroup newGroup = SProjectRecruitGroup.builder()
                    .post(post)
                    .name(groupInfo.getName().trim())
                    .recruitCount(groupInfo.getRecruitCount())
                    .build();

            sProjectRecruitGroupRepository.save(newGroup);
        }
    }

    // 사이드 프로젝트 게시글 수정
    public SProjectPost editSProjectPost(User user, SProjectPostEditRequest.PostInfo postInfo) {
        SProjectPost post = sProjectPostRepository.findByIdx(postInfo.getIdx()).orElseThrow(CContentNotFoundException::new);

        if(!post.getUser().getIdx().equals(user.getIdx()))
            throw new CAccessDeniedException();

        post.setTitle(postInfo.getTitle());
        post.setContent(postInfo.getContent());

        return sProjectPostRepository.save(post);
    }

    // 사이드 프로젝트 게시글 삭제
    public void deleteSProjectPost(User user, Long postIdx) {
        SProjectPost post = sProjectPostRepository.findByIdx(postIdx).orElseThrow(CContentNotFoundException::new);

        if(!post.getUser().getIdx().equals(user.getIdx()))
            throw new CAccessDeniedException();

        sProjectPostRepository.delete(post);
    }

    // 사이드 프로젝트 게시글에 관련된 지원자 모집 그룹 삭제
    public void deleteSProjectRecruitGroup(User user, SProjectPost post) {
        if(!post.getUser().getIdx().equals(user.getIdx()))
            throw new CAccessDeniedException();

        List<SProjectRecruitGroup> groups = sProjectRecruitGroupRepository.findBypost_Idx(post.getIdx()).orElseThrow(CContentNotFoundException::new);

        for(SProjectRecruitGroup group : groups) {
            sProjectRecruitGroupRepository.delete(group);
        }
    }
}
