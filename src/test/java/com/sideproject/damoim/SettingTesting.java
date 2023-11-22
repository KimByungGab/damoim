package com.sideproject.damoim;

import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.advice.exception.CUserNotFoundException;
import com.sideproject.damoim.controller.UserController;
import com.sideproject.damoim.dto.user.join.UserJoinRequest;
import com.sideproject.damoim.dto.user.login.UserLoginRequest;
import com.sideproject.damoim.dto.user.login.UserLoginResponse;
import com.sideproject.damoim.entity.SProjectPost;
import com.sideproject.damoim.entity.SProjectRecruitGroup;
import com.sideproject.damoim.entity.SProjectReply;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.repository.SProjectPostRepository;
import com.sideproject.damoim.repository.SProjectRecruitGroupRepository;
import com.sideproject.damoim.repository.SProjectReplyRepository;
import com.sideproject.damoim.repository.UserRepository;
import com.sideproject.damoim.response.SingleResult;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
public class SettingTesting {

    private final String email = "qudrkq2237@test.com";
    private final String password = "12345";
    private final String nickname = "테스트_사용자";
    private String accessToken;
    private String refreshToken;

    // UserController 테스트 이전 세팅
    public void initBeforeUserTest(UserController userController) {

        addTestUser(userController);
        loginTestUser(userController);
    }

    // SProjectPost 테스트 이전 세팅
    public void initBeforeSProjectPostTest(UserController userController,
                                       UserRepository userRepository,
                                       SProjectPostRepository sProjectPostRepository,
                                       SProjectRecruitGroupRepository sProjectRecruitGroupRepository) {
        addTestUser(userController);
        loginTestUser(userController);
        createDummySProjectPost(userRepository, sProjectPostRepository, sProjectRecruitGroupRepository);
    }

    // SProjectReply 테스트 이전 세팅
    public void initBeforeSProjectReplyTest(UserController userController,
                                            UserRepository userRepository,
                                            SProjectPostRepository sProjectPostRepository,
                                            SProjectRecruitGroupRepository sProjectRecruitGroupRepository,
                                            SProjectReplyRepository sProjectReplyRepository) {
        addTestUser(userController);
        loginTestUser(userController);
        createDummySProjectPost(userRepository, sProjectPostRepository, sProjectRecruitGroupRepository);
        createDummySProjectReply(userRepository, sProjectPostRepository, sProjectReplyRepository);
    }

    // SProjectVolunteer 테스트 이전 세팅
    public void initBeforeSProjectVolunteerTest(UserController userController,
                                                UserRepository userRepository,
                                                SProjectPostRepository sProjectPostRepository,
                                                SProjectRecruitGroupRepository sProjectRecruitGroupRepository) {
        addTestUser(userController);
        loginTestUser(userController);
        createDummySProjectPost(userRepository, sProjectPostRepository, sProjectRecruitGroupRepository);
    }

    // 테스트 유저 생성
    private void addTestUser(UserController userController) {

        // 회원 가입
        UserJoinRequest userJoinRequest = new UserJoinRequest();
        userJoinRequest.setEmail(email);
        userJoinRequest.setNickname(nickname);
        userJoinRequest.setPasswordCheck(password);
        userJoinRequest.setPassword(password);

        userController.join(userJoinRequest);
    }

    // 테스트 유저 로그인
    private void loginTestUser(UserController userController) {

        // 로그인 후 토큰 값 저장
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(email);
        userLoginRequest.setPassword(password);

        SingleResult loginUserInfo = userController.login(userLoginRequest);
        UserLoginResponse userLoginResponse = (UserLoginResponse) loginUserInfo.getData();

        accessToken = userLoginResponse.getAccessToken();
        refreshToken = userLoginResponse.getRefreshToken();
    }

    // 테스트용 사이드 프로젝트 게시글 정보 insert
    private void createDummySProjectPost(UserRepository userRepository, SProjectPostRepository sProjectPostRepository, SProjectRecruitGroupRepository sProjectRecruitGroupRepository) {

        User user = userRepository.findByEmail(email).orElseThrow(CUserNotFoundException::new);

        // 사이드 프로젝트 게시글 DB 저장
        SProjectPost sProjectPost = SProjectPost.builder()
                .user(user)
                .title("createDummySProjectPost 제목1")
                .content("createDummySProjectPost 내용1")
                .isOpened(true)
                .createdAt(LocalDateTime.now())
                .build();

        SProjectPost newPost = sProjectPostRepository.save(sProjectPost);

        SProjectRecruitGroup group = SProjectRecruitGroup.builder()
                .post(newPost)
                .name("createDummySProjectPost 지원 그룹 이름")
                .recruitCount(1)
                .build();

        sProjectRecruitGroupRepository.save(group);
    }

    // 테스트용 사이드 프로젝트 게시글의 댓글 정보 insert
    private void createDummySProjectReply(UserRepository userRepository, SProjectPostRepository postRepository, SProjectReplyRepository replyRepository) {

        User user = userRepository.findByEmail(email).orElseThrow(CUserNotFoundException::new);
        Page<SProjectPost> posts = postRepository.findByuser_idx(user.getIdx(), PageRequest.of(0, 1));
        if(!posts.hasContent())
            throw new CContentNotFoundException();

        SProjectReply sProjectReply = SProjectReply.builder()
                .user(user)
                .sProjectPost(posts.getContent().get(0))
                .content("createDummySProjectReply")
                .parentReplyIdx(0L)
                .createdAt(LocalDateTime.now())
                .build();

        replyRepository.save(sProjectReply);
    }
}
