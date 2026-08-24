package com.snipify.snipify.security;

import com.snipify.snipify.Enums.AuthProviderTypes;
import com.snipify.snipify.Enums.Roles;
import com.snipify.snipify.dto.LoginResponseDto;
import com.snipify.snipify.model.Pro;
import com.snipify.snipify.model.User;
import com.snipify.snipify.repo.UserRepository;
import com.snipify.snipify.service.UserDetailsImpl;
import com.snipify.snipify.util.JwtUtils;
import com.snipify.snipify.util.OAuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtils jwtUtils;
    private final OAuthUtil oAuthUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper =new ObjectMapper();
    @Value("${app.security.cookie-secure}")
    private boolean isCookieSecure;

    @Value("${app.frontend.url}")
    private String frontendUrl;


    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User= (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oAuth2AuthenticationToken= (OAuth2AuthenticationToken) authentication;
        String registrationId=oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        String providerId=oAuthUtil.getProviderIdFromOAuth2User(oAuth2User,registrationId);
        AuthProviderTypes providerType=oAuthUtil.getProviderTypeFromRegistrationId(registrationId);

        String email=oAuthUtil.getEmailFromOAuth2User(oAuth2User);
        if (email==null||email.isBlank()){
            throw new BadCredentialsException(
                    "Unable to fetch the email due to private setting on GitHub. Please make your email public."
            );
        }

        User user=userRepository.findByEmailOrProviderId(email,providerId).orElse(null);
        if (user == null) {
            user = new User();
            user.setProviderId(providerId);
            user.setEmail(email);
            user.setProviderTypes(providerType);
            user.setRoles(Set.of(Roles.PRO));
            user.setUsername(oAuth2User.getAttribute("name"));

            Pro pro = new Pro();
            pro.setUser(user);
            pro.setSubdomainName(null);
            user.setPro(pro);

            userRepository.saveAndFlush(user);
        }

     else if (!user.getProviderTypes().equals(providerType)) {
        throw new BadCredentialsException("User already exists with a different authentication provider.");
    }
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String jwt = jwtUtils.generateToken(userDetails);
        LoginResponseDto loginResponseDto = new LoginResponseDto(userDetails.getId(), jwt);

        ResponseCookie responseCookie=ResponseCookie.from("jwt",jwt)
                        .httpOnly(true)
                                .secure(isCookieSecure)
                                        .maxAge(24*60*60)
                                                .path("/")
                                                        .sameSite("Strict")
                                                                .build();

        response.addHeader(HttpHeaders.SET_COOKIE,responseCookie.toString());
        boolean isOnboardingComplete=(user.getPro() != null && user.getPro().getSubdomainName() != null);

        String targetRoute=isOnboardingComplete ? "/dashboard" : "/onboard";
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + targetRoute);

        objectMapper.writeValue(response.getWriter(), loginResponseDto);


    }
}
