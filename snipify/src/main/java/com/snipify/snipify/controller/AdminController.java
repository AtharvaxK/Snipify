package com.snipify.snipify.controller;

import com.snipify.snipify.dto.AdminDashboardDTO;
import com.snipify.snipify.dto.UserDashboardDto;
import com.snipify.snipify.dto.UserInfoDto;
import com.snipify.snipify.model.User;
import com.snipify.snipify.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> adminDashboard() {
        AdminDashboardDTO adminDashboardDTO = adminService.adminDashboard();

        if (adminDashboardDTO != null) {
            return new ResponseEntity<>(adminDashboardDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserInfoDto>> getAllUsers(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        Page<UserInfoDto> page = adminService.getAllUser(pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {

        adminService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/users/email/{email}")
    public ResponseEntity<?> deleteByEmail(@PathVariable String email) {

        adminService.deleteUserByEmail(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/users/username/{username}")
    public ResponseEntity<?> deleteByUsername(@PathVariable String username) {

        adminService.deleteUserByUsername(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/userDashboard/{id}")
    public ResponseEntity<UserDashboardDto> userDashboard(@PathVariable long id) {
        UserDashboardDto userDashboardDto = adminService.UserDashboard(id);

        if (userDashboardDto != null) {
            return new ResponseEntity<>(userDashboardDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<UserInfoDto>> searchUser(@RequestParam String keyword, @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        Page<UserInfoDto> page = adminService.getUserBySearch(keyword, pageable);

            return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
