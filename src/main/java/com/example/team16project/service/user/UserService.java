package com.example.team16project.service.user;

import com.example.team16project.domain.user.User;
import com.example.team16project.dto.user.AddUserRequest;
import com.example.team16project.dto.user.UpdateUserPasswordRequest;
import com.example.team16project.dto.user.UserInfo;
import com.example.team16project.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public void save(AddUserRequest request){
        User user =  userRepository.save(
        User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("JUNIOR")
                .build()
        );
    }

    public void checkEmailDuplicate(String email) throws DataIntegrityViolationException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new DataIntegrityViolationException("이미 사용중인 이메일입니다. 다른 이메일을 입력해주세요");
        }
    }

    public void checkNicknameDuplicate(String nickname) throws DataIntegrityViolationException {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if (optionalUser.isPresent()) {
            throw new DataIntegrityViolationException("이미 사용중인 닉네임입니다.");
        }
    }

    public UserInfo findUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Optional<User> byNickname = userRepository.findByNickname(user.getNickname());
        User user1 = byNickname.get();
        UserInfo build = UserInfo.builder().nickname(user1.getNickname()).email(user1.getEmail()).profileImage(user1.getProfileImage()).build();
        return build;
    }

    @Transactional
    public String updatePassword(UpdateUserPasswordRequest request, Authentication authentication) {
      
        User loginUser = (User)authentication.getPrincipal();
        User registeredUser = userRepository.findById(loginUser.getUserId()).get();
        if (!encoder.matches(request.getCurrentPassword(), registeredUser.getPassword())) {
            return "현재 비밀번호가 일치하지 않습니다.";
        }

        registeredUser.updatePassword(encoder.encode(request.getNewPassword()));
        return "비밀번호 변경이 완료되었습니다.";
    }

    public boolean isDeleted(Authentication authentication) {
        User loginUser = (User) authentication.getPrincipal();
        return userRepository.findByEmail(loginUser.getEmail()).get().getDeletedAt() != null;
    }

    @Transactional
    public void recoveryUser(Authentication authentication) {
        User deletedUser = (User) authentication.getPrincipal();
        User registeredUser = userRepository.findByEmail(deletedUser.getEmail()).get();
        registeredUser.recovery();
    }

    public void updateNickname(String nickname, Authentication authentication) {
        User CurrentUser = (User) authentication.getPrincipal();
        User registeredUser = userRepository.findByNickname(CurrentUser.getNickname()).get();
    }
}

