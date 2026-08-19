package com.snipify.snipify.util;

import com.snipify.snipify.Enums.AuthProviderTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OAuthUtil {

    public AuthProviderTypes getProviderTypeFromRegistrationId(String registrationId){
            switch (registrationId.toLowerCase()){
                case "google":
                    return AuthProviderTypes.GOOGLE;

                case "github":
                    return AuthProviderTypes.GITHUB;

                default:
                    throw new IllegalArgumentException("Unsupported OAuth2 Provider : "+registrationId);
            }
    }

    public String getProviderIdFromOAuth2User(OAuth2User oAuth2User,String registrationId){
        String providerId=switch (registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> {
                Object id=oAuth2User.getAttribute("id");
                if(id!=null){
                    yield String.valueOf(id);
                }
                else {
                    yield null;
                }
            }
            default ->{
                log.info("Unsupported Oauth2 provider ID {} " ,registrationId);
                throw new IllegalArgumentException("Unsupported OAuth2 Provider "+registrationId);
            }
        };

        if(providerId==null){
            log.info("Unable to fetch provider : {}",registrationId);
            throw new IllegalArgumentException("Unable to fetch Provider for OAuth2");
        }
        return providerId;
    }

    public String getEmailFromOAuth2User(OAuth2User oAuth2User){
        String email=oAuth2User.getAttribute("email");

        if (email!=null&&!email.isBlank()){
            return email;
        }

        return null;
    }
}
