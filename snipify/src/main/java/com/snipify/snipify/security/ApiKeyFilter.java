package com.snipify.snipify.security;

import com.snipify.snipify.Enums.Status;
import com.snipify.snipify.model.API_User;
import com.snipify.snipify.model.User;
import com.snipify.snipify.repo.API_UserRepository;
import com.snipify.snipify.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final API_UserRepository apiUserRepository;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKey= request.getHeader("X-API-KEY");

        if(apiKey==null||!apiKey.startsWith("snipify/")){
            filterChain.doFilter(request,response);
            return;
        }

        try {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            byte []hash=messageDigest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
            String hashedKey=HexFormat.of().formatHex(hash);

            Optional<API_User> apiUser=apiUserRepository.findByApiKeyAndStatus(hashedKey,Status.ACTIVE);
            if (apiUser.isPresent()){
                User user=apiUser.get().getUser();

                UserDetailsImpl userDetails=UserDetailsImpl.build(user);

                UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
        filterChain.doFilter(request, response);
    }
}
