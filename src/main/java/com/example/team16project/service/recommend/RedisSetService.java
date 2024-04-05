package com.example.team16project.service.recommend;

import com.example.team16project.domain.user.User;
import com.example.team16project.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class RedisSetService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    public void addRecommendToSet(String articleId, String writer) {
        User user = userRepository.findByNickname(writer).get();
        redisTemplate.opsForSet().add(articleId, String.valueOf(user.getUserId()));

    }

    public Set<String> getset(String userId) {
        return redisTemplate.opsForSet().members(userId);
    }
}