package com.sideproject.damoim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.advice.exception.CUserNotFoundException;
import com.sideproject.damoim.controller.UserController;
import com.sideproject.damoim.dto.sProject.reply.SProjectReplyCreateRequest;
import com.sideproject.damoim.dto.sProject.reply.SProjectReplyEditRequest;
import com.sideproject.damoim.entity.SProjectPost;
import com.sideproject.damoim.entity.SProjectReply;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.repository.SProjectPostRepository;
import com.sideproject.damoim.repository.SProjectRecruitGroupRepository;
import com.sideproject.damoim.repository.SProjectReplyRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SProjectReplyTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SProjectPostRepository sProjectPostRepository;

    @Autowired
    private SProjectReplyRepository sProjectReplyRepository;

    @Autowired
    private SProjectRecruitGroupRepository sProjectRecruitGroupRepository;

    private final SettingTesting setting = new SettingTesting();

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper om = new ObjectMapper();

    @BeforeAll
    void beforeAll() {
        setting.initBeforeSProjectReplyTest(userController,
                userRepository,
                sProjectPostRepository,
                sProjectRecruitGroupRepository,
                sProjectReplyRepository);
    }

    @AfterAll
    void afterAll() {
        sProjectReplyRepository.deleteAll();
        sProjectRecruitGroupRepository.deleteAll();
        sProjectPostRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("댓글 작성")
    @Test
    public void createSProjectReplyTest() throws Exception {

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

        // 댓글 작성
        SProjectReplyCreateRequest request = new SProjectReplyCreateRequest();
        request.setPostIdx(Long.parseLong(postIdxString));
        request.setContent("댓글 내용1");
        request.setParentReplyIdx(Long.parseLong("0"));

        // 정상이면 Created
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/SPost/reply/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + setting.getAccessToken())
                                .content(om.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DisplayName("댓글 불러오기")
    @Test
    public void getSProjectPostReplyTest() throws Exception {
        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);

        Page<SProjectPost> pagePosts = sProjectPostRepository.findByuser_idx(user.getIdx(), PageRequest.of(0, 1));
        if(pagePosts.isEmpty())
            throw new CContentNotFoundException();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/SPost/reply")
                                .param("postIdx", pagePosts.getContent().get(0).getIdx().toString())
                                .param("parentReplyIdx", "0")
                                .param("page", "1")
                                .param("pageSize", "1")
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("댓글 수정")
    @Test
    public void editSProjectReplyTest() throws Exception {
        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        SProjectReply myReply = sProjectReplyRepository.findByuser_Idx(user.getIdx()).orElseThrow(CContentNotFoundException::new);

        SProjectReplyEditRequest request = new SProjectReplyEditRequest();
        request.setContent("editSProjectReplyTest");
        request.setReplyIdx(myReply.getIdx());

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/SPost/reply/edit")
                                .header("Authorization", "Bearer " + setting.getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(request))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("댓글 삭제")
    @Test
    public void deleteSProjectReplyTest() throws Exception {
        User user = userRepository.findByEmail(setting.getEmail()).orElseThrow(CUserNotFoundException::new);
        SProjectReply myReply = sProjectReplyRepository.findByuser_Idx(user.getIdx()).orElseThrow(CContentNotFoundException::new);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/SPost/reply/delete")
                                .header("Authorization", "Bearer " + setting.getAccessToken())
                                .param("replyIdx", myReply.getIdx().toString())
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
