package com.snipify.snipify.repo;

import com.snipify.snipify.model.Pro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProRepository extends JpaRepository<Pro, Long> {
    boolean existsBySubdomainName(String subDomain);
}