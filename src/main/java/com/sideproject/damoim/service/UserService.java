package com.sideproject.damoim.service;

import com.sideproject.damoim.advice.exception.CAuthenticationEntryPointException;
import com.sideproject.damoim.advice.exception.CUserNotFoundException;
import com.sideproject.damoim.dto.user.join.UserJoinRequest;
import com.sideproject.damoim.dto.user.login.UserLoginRequest;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.entity.UserRole;
import com.sideproject.damoim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    // 이메일 중복 체크
    public boolean checkEmailDuplicate(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    // 닉네임 중복 체크
    public boolean checkNicknameDuplicate(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.isPresent();
    }

    // 회원 가입
    public boolean joinUser(UserJoinRequest request) {
        if(!request.getPassword().equals(request.getPasswordCheck())) {
            return false;
        }

        User user = User.builder()
                .email(request.getEmail().trim())
                .password(encoder.encode(request.getPassword().trim()))
                .nickname(request.getNickname().trim())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        return true;
    }

    // 로그인
    public User login(UserLoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty())
            return null;

        User user = optionalUser.get();

        if(!encoder.matches(request.getPassword(), user.getPassword()))
            return null;

        return user;
    }

    // 유저 인덱스로 유저 검색
    public User getUserByUserIdx(Long userIdx) {
        if(userIdx == null)
            return null;

        Optional<User> optionalUser = userRepository.findById(userIdx);
        if(optionalUser.isEmpty())
            return null;

        return optionalUser.get();
    }

    // 이메일로 유저 검색
    public User getUserByEmail(String email) {
        if(email == null)
            return null;

        Optional<User> optionalUser = userRepository.findByEmail(email.trim());
        if(optionalUser.isEmpty())
            return null;

        return optionalUser.get();
    }

    // 닉네임으로 유저 검색
    public User getUserByNickname(String nickname) {
        if(nickname == null)
            return null;

        Optional<User> optionalUser = userRepository.findByNickname(nickname.trim());
        if(optionalUser.isEmpty())
            return null;

        return optionalUser.get();
    }

    // 인증 정보로 유저 조회
    public User getLoginUserInfo(Authentication auth) {
        if(auth == null)
            throw new CAuthenticationEntryPointException();

        User loginUser = getUserByEmail(auth.getName());
        if(loginUser == null)
            throw new CUserNotFoundException();

        return loginUser;
    }
}
