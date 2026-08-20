package com.snipify.snipify.repo;

import com.snipify.snipify.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;



import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Boolean existsByUsername(String username);

    @Override
    @EntityGraph(attributePaths = {"pro", "roles"})
    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);


    boolean existsByEmail(String email);


    Optional<User> findByEmailOrProviderId(String email, String providerId);



    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"pro", "roles"})
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable  pageable);

    @EntityGraph(attributePaths = {"pro", "urls", "urls.clickAnalytics"})
    Optional<User> findForDeletionByUsername(String username);

    @EntityGraph(attributePaths = {"pro", "urls", "urls.clickAnalytics"})
    Optional<User> findForDeletionByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.email = :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("newPassword") String newPassword);

}