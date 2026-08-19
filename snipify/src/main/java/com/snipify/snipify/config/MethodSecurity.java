package com.snipify.snipify.config;


import com.snipify.snipify.repo.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("urlSecurity")
@RequiredArgsConstructor
public class MethodSecurity {

    private final UrlRepository urlRepository;

    public boolean isOwner(long urlId,long userId){
        return urlRepository.existsByUser_IdAndUrlId(userId,urlId);
    }
}
