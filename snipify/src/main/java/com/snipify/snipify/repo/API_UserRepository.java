package com.snipify.snipify.repo;

import com.snipify.snipify.Enums.Status;
import com.snipify.snipify.model.API_User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface API_UserRepository extends JpaRepository<API_User, Long> {

  @EntityGraph(attributePaths = {"user","user.roles"})
  @Query("SELECT a FROM API_User a WHERE a.apiKey = :apiKey AND a.status = :status")
  Optional<API_User> findByApiKeyAndStatus(@Param("apiKey") String apiKey, @Param("status") Status status);
}