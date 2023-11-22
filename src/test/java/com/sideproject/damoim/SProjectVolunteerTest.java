package com.sideproject.damoim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.advice.exception.CUserNotFoundException;
import com.sideproject.damoim.controller.UserController;
import com.sideproject.damoim.dto.user.join.UserJoinRequest;
import com.sideproject.damoim.dto.user.login.UserLoginRequest;
import com.sideproject.damoim.dto.user.login.UserLoginResponse;
import com.sideproject.damoim.entity.SProjectPost;
import com.sideproject.damoim.entity.SProjectRecruitGroup;
import com.sideproject.damoim.entity.SProjectVolunteer;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SProjectVolunteerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SProjectPostRepository sProjectPostRepository;

    @Autowired
    private SProjectRecruitGroupRepository sProjectRecruitGroupRepository;

    @Autowired
    private SProjectVolunteerRepository sProjectVolunteerRepository;

    private final SettingTesting setting = new SettingTesting();

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper om = new ObjectMapper();

    private String volunteerAccessToken;

    @BeforeAll
    void beforeAll() {
        setting.initBeforeSProjectVolunteerTest(userController,
                userRepository,
                sProjectPostRepository,
                sProjectRecruitGroupRepository);

        String volunteerEmail = "volunteer1@test.com";
        String volunteerNickname = "volunteer1";
        String volunteerPw = "volunteer1";

        UserJoinRequest userJoinRequest = new UserJoinRequest();
        userJoinRequest.setEmail(volunteerEmail);
        userJoinRequest.setNickname(volunteerNickname);
        userJoinRequest.setPasswordCheck(volunteerPw);
        userJoinRequest.setPassword(volunteerPw);

        userController.join(userJoinRequest);

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(volunteerEmail);
        userLoginRequest.setPassword(volunteerPw);
        UserLoginResponse response = (UserLoginResponse) userController.login(userLoginRequest).getData();
        volunteerAccessToken = response.getAccessToken();
    }

    @AfterAll
    void afterAll() {
        sProjectRecruitGroupRepository.deleteAll();
        sProjectPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("현재 지원 상태 확인")
    @Test
    public void getCurrentRecruitGroupStatusTest() throws Exception {

        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        SProjectPost post = sProjectPostRepository.findByuser_idx(user.getIdx(), PageRequest.of(0, 1))
                .getContent().get(0);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/SPost/volunteer/status")
                        .param("postIdx", post.getIdx().toString())
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("지원 신청")
    @Test
    public void applyVolunteerTest() throws Exception {

        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        Page<SProjectRecruitGroup> pageRandomGroup = sProjectRecruitGroupRepository.findAll(PageRequest.of(0, 1));

        if(pageRandomGroup.isEmpty())
            throw new CContentNotFoundException();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/SPost/volunteer/apply")
                        .header("Authorization", "Bearer " + volunteerAccessToken)
                        .param("groupIdx", pageRandomGroup.getContent().get(0).getIdx().toString())
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/SPost/volunteer/apply")
                                .header("Authorization", "Bearer " + setting.getAccessToken())
                                .param("groupIdx", pageRandomGroup.getContent().get(0).getIdx().toString())
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @DisplayName("지원 취소")
    @Test
    public void cancelApplicationVolunteerTest() throws Exception {

        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        Page<SProjectRecruitGroup> pageRandomGroup = sProjectRecruitGroupRepository.findAll(PageRequest.of(0, 1));

        if(pageRandomGroup.isEmpty())
            throw new CContentNotFoundException();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/SPost/volunteer/apply")
                                .header("Authorization", "Bearer " + volunteerAccessToken)
                                .param("groupIdx", pageRandomGroup.getContent().get(0).getIdx().toString())
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/SPost/volunteer/cancel")
                        .header("Authorization", "Bearer " + volunteerAccessToken)
                        .param("groupIdx", pageRandomGroup.getContent().get(0).getIdx().toString())
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("지원 승낙")
    @Test
    public void acceptVolunteerTest() throws Exception {
        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        Page<SProjectPost> pageMyPost = sProjectPostRepository.findByuser_idx(user.getIdx(), PageRequest.of(0, 1));
        SProjectPost myPost = pageMyPost.getContent().get(0);
        List<SProjectRecruitGroup> groupList = sProjectRecruitGroupRepository.findBypost_Idx(myPost.getIdx()).orElseThrow(CContentNotFoundException::new);
        SProjectRecruitGroup randomGroup = groupList.get(0);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/SPost/volunteer/apply")
                                .header("Authorization", "Bearer " + volunteerAccessToken)
                                .param("groupIdx", randomGroup.getIdx().toString())
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        List<SProjectVolunteer> volunteerList = sProjectVolunteerRepository.findAllBysProjectRecruitGroup_Idx(randomGroup.getIdx()).orElseThrow(CContentNotFoundException::new);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/SPost/volunteer/accept")
                        .header("Authorization", "Bearer " + setting.getAccessToken())
                        .param("volunteerIdx", volunteerList.get(0).getIdx().toString())
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("지원 거절")
    @Test
    public void declineVolunteerTest() throws Exception {
        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        Page<SProjectPost> pageMyPost = sProjectPostRepository.findByuser_idx(user.getIdx(), PageRequest.of(0, 1));
        SProjectPost myPost = pageMyPost.getContent().get(0);
        List<SProjectRecruitGroup> groupList = sProjectRecruitGroupRepository.findBypost_Idx(myPost.getIdx()).orElseThrow(CContentNotFoundException::new);
        SProjectRecruitGroup randomGroup = groupList.get(0);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/SPost/volunteer/apply")
                                .header("Authorization", "Bearer " + volunteerAccessToken)
                                .param("groupIdx", randomGroup.getIdx().toString())
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        List<SProjectVolunteer> volunteerList = sProjectVolunteerRepository.findAllBysProjectRecruitGroup_Idx(randomGroup.getIdx()).orElseThrow(CContentNotFoundException::new);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/SPost/volunteer/decline")
                                .header("Authorization", "Bearer " + setting.getAccessToken())
                                .param("volunteerIdx", volunteerList.get(0).getIdx().toString())
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
