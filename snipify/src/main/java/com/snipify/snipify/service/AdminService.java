package com.snipify.snipify.service;

import com.snipify.snipify.CustomExceptions.UserNotFoundException;
import com.snipify.snipify.dto.AdminDashboardDTO;
import com.snipify.snipify.dto.UserDashboardDto;
import com.snipify.snipify.dto.UserInfoDto;
import com.snipify.snipify.model.ClickAnalytics;
import com.snipify.snipify.model.User;
import com.snipify.snipify.repo.ClickAnalyticsRepository;
import com.snipify.snipify.repo.UrlRepository;
import com.snipify.snipify.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {

    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final UrlRepository urlRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminDashboardDTO adminDashboard(){
        AdminDashboardDTO adminDashboardDTO=new AdminDashboardDTO();
        List<Object[]> list=clickAnalyticsRepository.highestCountryByClicksGlobal();
        Map<String,Long>map=new LinkedHashMap<>();

        for(Object[] obj: list){
            String country=(String) obj[0];
            Long count=(Long) obj[1];
            map.put(country,count);
        }
        adminDashboardDTO.setCountryAnalytics(map);
        adminDashboardDTO.setTotalClicks(clickAnalyticsRepository.count());
        adminDashboardDTO.setTotalUrls(urlRepository.count());
        adminDashboardDTO.setTotalUsers(userRepository.count());
        adminDashboardDTO.setTotalClicksIn30Days(clickAnalyticsRepository.totalClicksByTimeGlobal(LocalDateTime.now().minusDays(30)));

        return adminDashboardDTO;
    }
    @Transactional(readOnly = true)
    public Page<UserInfoDto> getAllUser(Pageable pageable){
        Page<User> user=userRepository.findAll(pageable);
        return user.map(this::convertToDto);
    }

    @Transactional
    public void deleteUserById(long id){
        boolean doExist=userRepository.existsById(id);
        if (doExist){
            userRepository.deleteById(id);
        }
        else{
            throw new UserNotFoundException("User does not exist with this User Id");
        }
    }

    @Transactional
    public void deleteUserByUsername(String username){
        User user = userRepository.findForDeletionByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User does not exist with this User Username"));
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserByEmail(String email){
        User user = userRepository.findForDeletionByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exist with this User Email"));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserDashboardDto UserDashboard(long userId) {
        UserDashboardDto userDashboardDto = new UserDashboardDto();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User does not exist with ID: " + userId));
        userDashboardDto.setUsername(user.getUsername());
        userDashboardDto.setEmail(user.getEmail());
        userDashboardDto.setSubdomain(user.getPro().getSubdomainName());
        userDashboardDto.setTotalUrls(urlRepository.countByUser_Id(user.getId()));
        userDashboardDto.setTotalClicksIn30day(clickAnalyticsRepository.countClicksByTime(userId, LocalDateTime.now().minusDays(30)));
        List<Object[]> list = clickAnalyticsRepository.highestCountryByClicks(userId);
        Map<String, Long> map = new LinkedHashMap<>();

        for (Object[] obj : list) {
            map.put((String) obj[0], (Long) obj[1]);
        }
        userDashboardDto.setCountryAnalytics(map);
        return userDashboardDto;
    }

    @Transactional(readOnly = true)
    public Page<UserInfoDto> getUserBySearch(String keyword, Pageable pageable){
      Page<User> user=userRepository.searchUsers(keyword,pageable);

       return user.map(this::convertToDto);
    }

    public UserInfoDto convertToDto(User user){
        String subdomain=(user.getPro()!=null)?user.getPro().getSubdomainName():null;
        return new UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                subdomain

        );
    }




}
