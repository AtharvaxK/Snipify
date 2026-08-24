package com.snipify.snipify.repo;

import com.snipify.snipify.model.Pro;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProRepository extends JpaRepository<Pro, Long> {
    boolean existsBySubdomainName(String subDomain);

    @Modifying
    @Transactional
    @Query("UPDATE Pro p SET p.subdomainName= :newSubdomain WHERE p.id= :id")
    void updateSubdomainById(@Param("newSubdomain") String subdomain, @Param("id") long id);
}