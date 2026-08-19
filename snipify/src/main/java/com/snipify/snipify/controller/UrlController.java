package com.snipify.snipify.controller;

import com.snipify.snipify.CustomExceptions.LinkExpiredException;
import com.snipify.snipify.CustomExceptions.LinkNotFoundException;
import com.snipify.snipify.dto.ShortenUrlRequest;
import com.snipify.snipify.model.User;
import com.snipify.snipify.service.UrlService;
import com.snipify.snipify.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {
    private UrlService urlService;

    @Autowired
    public void setUrlService(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode, HttpServletRequest httpServletRequest){
        return handleRedirect( null,shortCode, httpServletRequest);
    }

    @GetMapping("/{subdomain}/{shortCode}")
    public ResponseEntity<?> redirectSubdomain(@PathVariable("subdomain") String subdomain, @PathVariable("shortCode") String shortCode, HttpServletRequest httpServletRequest) {
        return handleRedirect(subdomain, shortCode, httpServletRequest);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortUrl(@RequestBody ShortenUrlRequest shortenUrlRequest , @AuthenticationPrincipal UserDetailsImpl userDetails){

        User user;
        if(userDetails!=null){
            user=userDetails.getUser();
        }
        else {
            user=null;
        }

        String shorturl=urlService.shortenUrl(shortenUrlRequest.getUrl(),shortenUrlRequest.getValidTime(),user);
        if(user!=null&&user.getPro()!=null&&user.getPro().getSubdomainName()!=null){
            String finalShortUrl="http://localhost:8080/" +user.getPro().getSubdomainName()+"/"+shorturl;
            return ResponseEntity.status(HttpStatus.CREATED).body(finalShortUrl);
        }
            String finalShortUrl = "http://localhost:8080/"  + shorturl;

        return ResponseEntity.status(HttpStatus.CREATED).body(finalShortUrl);
    }

    private ResponseEntity<?> handleRedirect(String subdomain, String shortCode, HttpServletRequest httpServletRequest) {
        try {
            System.out.println("LOOKUP -> Subdomain: " + subdomain + " | ShortCode: " + shortCode);
            String longUrl = urlService.longUrlMapping(subdomain, shortCode, httpServletRequest);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
        } catch (LinkExpiredException expiredException) {
            return ResponseEntity.status(HttpStatus.GONE).body(expiredException.getMessage());
        } catch (LinkNotFoundException linkNotFoundException) {
            System.out.println("LOOKUP -> Subdomain: " + subdomain + " | ShortCode: " + shortCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(linkNotFoundException.getMessage());
        }
    }
}
