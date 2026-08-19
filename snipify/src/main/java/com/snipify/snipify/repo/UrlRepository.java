package com.snipify.snipify.repo;

import com.snipify.snipify.Enums.Status;
import com.snipify.snipify.model.Url;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Modifying
    @Query("UPDATE Url u SET u.status=?1 WHERE u.expiryTime<?2")
    void cleanExpired(Status status , LocalDateTime localDateTime);




    @Modifying
    @Query("UPDATE Url u SET u.status = :status WHERE u.urlId = :urlId")
    void updateStatus(@Param("status") Status status, @Param("urlId") Long urlId);

    @Modifying
    @Transactional
    @Query("UPDATE Url u SET u.clickCount = u.clickCount + :clickCount WHERE u.urlId = :urlId")
    void updateClickCount(@Param("clickCount") long clickCount, @Param("urlId") Long urlId);

    @Modifying
    @Query("UPDATE Url u SET u.shortCode = :shortCode WHERE u.urlId = :urlId")
    void updateShortCode(@Param("shortCode") String shortCode, @Param("urlId") Long urlId);




    Page<Url> findByUser_Id(long userId, Pageable pageable);

    long countByUser_Id(Long id);


    boolean existsByUser_IdAndUrlId(Long userId, Long urlId);
}