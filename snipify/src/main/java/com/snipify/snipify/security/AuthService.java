package com.snipify.snipify.security;

import com.snipify.snipify.Enums.AuthProviderTypes;
import com.snipify.snipify.Enums.Roles;
import com.snipify.snipify.dto.LoginRequestDto;
import com.snipify.snipify.dto.LoginResponseDto;
import com.snipify.snipify.dto.SignupRequestDto;
import com.snipify.snipify.dto.SignupResponseDto;
import com.snipify.snipify.model.Pro;
import com.snipify.snipify.model.User;
import com.snipify.snipify.repo.ProRepository;
import com.snipify.snipify.repo.UserRepository;
import com.snipify.snipify.service.UserDetailsImpl;
import com.snipify.snipify.util.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProRepository proRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public User signupInternal(SignupRequestDto  signupRequestDto , AuthProviderTypes authProviderTypes, String ProviderId){


        if(userRepository.existsByUsername(signupRequestDto.getUsername())){
            throw new BadCredentialsException("User already exist");
        }
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new BadCredentialsException("Email already exists");
        }
        String subDomain = signupRequestDto.getSubDomain();
        if (subDomain != null && !subDomain.isBlank()) {
            if (proRepository.existsBySubdomainName(subDomain)) {
                throw new BadCredentialsException("Subdomain already taken");
            }
        }
        User user=new User();
        user.setUsername(signupRequestDto.getUsername());
        user.setEmail(signupRequestDto.getEmail());
        user.setProviderTypes(authProviderTypes);
        user.setProviderId(ProviderId);
        user.setRoles(Set.of(Roles.PRO));

        if(authProviderTypes==AuthProviderTypes.EMAIL){
            user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        }

        userRepository.save(user);

        if (user.getRoles().contains(Roles.PRO)&&signupRequestDto.getSubDomain()!=null&& !signupRequestDto.getSubDomain().isBlank()){
            Pro pro=new Pro();
            pro.setSubdomainName(signupRequestDto.getSubDomain());
            pro.setUser(user);
            proRepository.save(pro);
            user.setPro(pro);
        }

        return user;
    }

    public SignupResponseDto signup(SignupRequestDto signupRequestDto){
        User user=signupInternal(signupRequestDto,AuthProviderTypes.EMAIL,null);

        return new SignupResponseDto(user.getId(),user.getUsername());
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken=jwtUtils.generateToken(userDetails);

        return new LoginResponseDto(userDetails.getId(),jwtToken);
    }


}
