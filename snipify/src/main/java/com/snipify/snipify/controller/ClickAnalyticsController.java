package com.snipify.snipify.controller;


import com.snipify.snipify.dto.UrlResponseDto;
import com.snipify.snipify.model.ClickAnalytics;
import com.snipify.snipify.model.Url;
import com.snipify.snipify.service.ClickAnalyticsService;
import com.snipify.snipify.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/analytics")
public class ClickAnalyticsController {


    private final ClickAnalyticsService clickAnalyticsService;

    @GetMapping("/urls")
    public ResponseEntity<Page<UrlResponseDto>> getAllUrlsByUser(@AuthenticationPrincipal UserDetailsImpl currentUser, @PageableDefault(page = 0,size = 10,sort = "urlId")Pageable pageable){
        Page<UrlResponseDto>list=clickAnalyticsService.getAllUrlByUser(currentUser.getId(),pageable);
        if(list.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/clickAnalytics/{urlId}")
    public ResponseEntity<Page<ClickAnalytics>> getAllClicksByUrl(@PathVariable long urlId, @PageableDefault(page = 0,size = 10,sort = "url.urlId")Pageable pageable){
        Page<ClickAnalytics> list=clickAnalyticsService.getAllClicksByUrlId(urlId,pageable);

        if(list.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/totalClicks")
    public ResponseEntity<Long> totalClicksByUser(@AuthenticationPrincipal UserDetailsImpl currentUser){
        long totalClicks= clickAnalyticsService.totalClicksByUser(currentUser.getId());
        return new ResponseEntity<>(totalClicks,HttpStatus.OK);
    }

    @GetMapping("/clicks30")
    public ResponseEntity<Long> totalClicksIn30Days(@AuthenticationPrincipal UserDetailsImpl currentUser){
        long totalClicks= clickAnalyticsService.clicksInLast30Days(currentUser.getId());
        return new ResponseEntity<>(totalClicks,HttpStatus.OK);
    }

    @GetMapping("/countryAnalytics")
    public ResponseEntity<Map<String,Long>> highestCountryByClickCount(@AuthenticationPrincipal UserDetailsImpl currUser){
        Map<String,Long>map=clickAnalyticsService.highestClicksByCountry(currUser.getId());

        if(map.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    @GetMapping("/clickByUrl/{urlId}")
    public ResponseEntity<Long> getClicksByUrl(@PathVariable long urlId){
        return new ResponseEntity<>(clickAnalyticsService.countTotalClicksOfUrl(urlId),HttpStatus.OK);
    }








}
