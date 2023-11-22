package com.sideproject.damoim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.advice.exception.CUserNotFoundException;
import com.sideproject.damoim.controller.UserController;
import com.sideproject.damoim.dto.sProject.post.GroupInfo;
import com.sideproject.damoim.dto.sProject.post.SProjectPostCreateRequest;
import com.sideproject.damoim.dto.sProject.post.SProjectPostEditRequest;
import com.sideproject.damoim.entity.SProjectPost;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.repository.SProjectPostRepository;
import com.sideproject.damoim.repository.SProjectRecruitGroupRepository;
import com.sideproject.damoim.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SProjectPostTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SProjectPostRepository sProjectPostRepository;

    @Autowired
    private SProjectRecruitGroupRepository sProjectRecruitGroupRepository;

    private final SettingTesting setting = new SettingTesting();

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper om = new ObjectMapper();

    @BeforeAll
    void beforeAll() {
        setting.initBeforeSProjectPostTest(userController, userRepository, sProjectPostRepository, sProjectRecruitGroupRepository);
    }

    @AfterAll
    void afterAll() {
        sProjectRecruitGroupRepository.deleteAll();
        sProjectPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("게시글 작성")
    @Test
    public void createSProjectPostTest() throws Exception {

        SProjectPostCreateRequest.PostInfo postInfo = new SProjectPostCreateRequest.PostInfo();
        postInfo.setTitle("테스트1");
        postInfo.setContent("테스트1 내용");

        List<GroupInfo> groupInfos = new ArrayList<>();
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setName("백엔드1");
        groupInfo.setRecruitCount(2);
        groupInfos.add(groupInfo);

        SProjectPostCreateRequest createRequest = new SProjectPostCreateRequest();
        createRequest.setPostInfo(postInfo);
        createRequest.setGroupInfos(groupInfos);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/SPost/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + setting.getAccessToken())
                        .content(om.writeValueAsString(createRequest))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DisplayName("게시글 조회")
    @Test
    public void getSProjectPostListTest() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/SPost/list")
                        .param("searchKeyword", "")
                        .param("page", "1")
                        .param("pageSize", "4")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/SPost/list")
                                .param("searchKeyword", "")
                                .param("page", "2")
                                .param("pageSize", "4")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("게시글 상세보기")
    @Test
    public void getSProjectPostInfoTest() throws Exception {

        // 게시글 리스트 정보 조회
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/SPost/list")
                                .param("searchKeyword", "")
                                .param("page", "1")
                                .param("pageSize", "4")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonNode = om.readTree(responseContent);
        String postIdxString = jsonNode.get("list").get(0).get("idx").asText();

        // 정상 처리 시 OK
        mockMvc.perform(
                MockMvcRequestBuilders.get("/SPost/detail")
                        .param("idx", postIdxString)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/SPost/detail")
                        .param("idx", "0")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("게시글 수정")
    @Test
    public void editSProjectPostTest() throws Exception {

        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        Page<SProjectPost> myPosts = sProjectPostRepository.findByuser_idx(user.getIdx(), PageRequest.of(0, 1));

        if(myPosts.isEmpty())
            throw new CContentNotFoundException();

        SProjectPost post = myPosts.getContent().get(0);

        SProjectPostEditRequest request = new SProjectPostEditRequest();

        SProjectPostEditRequest.PostInfo postInfo = new SProjectPostEditRequest.PostInfo();
        postInfo.setIdx(post.getIdx());
        postInfo.setTitle("editSProjectPostTest 제목");
        postInfo.setContent("editSProjectPostTest 내용");

        List<GroupInfo> groupInfos = new ArrayList<>();
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setName("editSProjectPostTest 지원 그룹 이름");
        groupInfo.setRecruitCount(2);
        groupInfos.add(groupInfo);

        request.setPostInfo(postInfo);
        request.setGroupInfos(groupInfos);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/SPost/edit")
                        .header("Authorization", "Bearer " + setting.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("게시글 삭제")
    @Test
    public void deleteSProjectPostTest() throws Exception {
        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        Page<SProjectPost> myPosts = sProjectPostRepository.findByuser_idx(user.getIdx(), PageRequest.of(0, 1));

        if(myPosts.isEmpty())
            throw new CContentNotFoundException();

        SProjectPost post = myPosts.getContent().get(0);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/SPost/delete")
                        .header("Authorization", "Bearer " + setting.getAccessToken())
                        .param("idx", post.getIdx().toString())
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
