package com.sideproject.damoim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.damoim.controller.UserController;
import com.sideproject.damoim.dto.NewTokenRequest;
import com.sideproject.damoim.dto.user.join.UserJoinRequest;
import com.sideproject.damoim.dto.user.login.UserLoginRequest;
import com.sideproject.damoim.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    private final SettingTesting setting = new SettingTesting();

    @Autowired
    private MockMvc mockMvc;
    ObjectMapper om = new ObjectMapper();

    @BeforeAll
    void beforeAll() {
        setting.initBeforeUserTest(userController);
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
    }

    @DisplayName("회원가입 테스트")
    @Test
    public void joinTest() throws Exception {

        UserJoinRequest userJoinRequest = new UserJoinRequest();
        userJoinRequest.setEmail("qudrkq2237@naver.com");
        userJoinRequest.setNickname("테스트2");
        userJoinRequest.setPasswordCheck("12345");
        userJoinRequest.setPassword("12345");

        // 정상 처리 시 OK
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userJoinRequest))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DisplayName("로그인 테스트")
    @Test
    public void loginTest() throws Exception {

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("qudrkq2237@test.com");
        userLoginRequest.setPassword("12345");

        // 정상 처리 시 OK
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userLoginRequest))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("유저 조회 테스트")
    @Test
    public void getUserInfoTest() throws Exception {

        // 정상 처리
        mockMvc.perform(
                MockMvcRequestBuilders.get("/user/info")
                        .param("nickname", setting.getNickname())
                        .header("Authorization", "Bearer " + setting.getAccessToken())
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("토큰 재발급 테스트")
    @Test
    public void getNewTokenTest() throws Exception {

        NewTokenRequest newTokenRequest = new NewTokenRequest();
        newTokenRequest.setRefreshToken(setting.getRefreshToken());

        // 정상 처리
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newTokenRequest))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
