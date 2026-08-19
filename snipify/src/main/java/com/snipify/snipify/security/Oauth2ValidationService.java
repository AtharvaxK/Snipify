//package com.snipify.snipify.security;
//
//import com.snipify.snipify.Enums.AuthProviderTypes;
//import com.snipify.snipify.Enums.Roles;
//import com.snipify.snipify.model.Pro;
//import com.snipify.snipify.model.User;
//import com.snipify.snipify.repo.UserRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.OAuth2Error;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Set;
//
//@Service
//@AllArgsConstructor
//public class Oauth2ValidationService extends DefaultOAuth2UserService {
//
//    private UserRepository userRepository;
//    private OAuthUtil oAuthUtil;
//
//
//    @Override
//    @Transactional
//    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException{
//        OAuth2User oAuth2User=super.loadUser(userRequest);
//        String registrationId=userRequest.getClientRegistration().getRegistrationId();
//
//        AuthProviderTypes providerType=oAuthUtil.getProviderTypeFromRegistrationId(registrationId);
//        String providerId=oAuthUtil.getProviderIdFromOAuth2User(oAuth2User,registrationId);
//        String email= oAuthUtil.getEmailFromOAuth2User(oAuth2User);
//
//        if (email==null||email.isBlank()){
//            throw new OAuth2AuthenticationException(
//                    new OAuth2Error("email_not_found"),
//                    "Unable to fetch the email due to private setting on GitHub. Please make your email public."
//            );
//        }
//
//        System.out.println("Inside loadUser");
//        System.out.println("Email = " + email);
//        System.out.println("ProviderId = " + providerId);
//
//
//        User loginUser=userRepository.findByEmailOrProviderId(email,providerId).orElse(null);
//        System.out.println("Found user = " + loginUser);
//        if(loginUser!=null){
//            System.out.println("user exist");
//           if (!loginUser.getProviderTypes().equals(providerType)){
//               throw new OAuth2AuthenticationException(
//                       new OAuth2Error("account_exists"),
//                       "User already exists with a different authentication provider."
//               );
//           }
//           else {
//
//               return oAuth2User;
//           }
//        }
//        else {
//            System.out.println("creating user");
//            User user=new User();
//            user.setProviderId(providerId);
//            user.setEmail(email);
//            user.setProviderTypes(providerType);
//            user.setRoles(Set.of(Roles.PRO));
//
//            Pro pro=new Pro();
//            pro.setUser(user);
//            user.setPro(pro);
//
//            try {
//                userRepository.saveAndFlush(user);
//            } catch (Exception e) {
//                e.printStackTrace();   // force it to surface
//                throw new OAuth2AuthenticationException(
//                        new OAuth2Error("save_failed"),
//                        "Failed to create user: " + e.getMessage()
//                );
//            }
//
//        }
//        return oAuth2User;
//    }
//}
