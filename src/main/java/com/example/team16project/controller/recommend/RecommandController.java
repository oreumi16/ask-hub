package com.example.team16project.controller.recommend;

import com.example.team16project.service.recommend.RedisSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class RecommandController {

    @Autowired
    private RedisSetService redisSetService;

    @GetMapping("/recommend")
    public Set<String> getrecommed() {

        return redisSetService.getset("user12");
    }


    @PostMapping("/recommend/{articleId}/{writer}")
    public ResponseEntity<String> recommed(@PathVariable String articleId, @PathVariable String writer) {
        redisSetService.addRecommendToSet(articleId, writer);
        return ResponseEntity.status(201).body("ok");
    }
}