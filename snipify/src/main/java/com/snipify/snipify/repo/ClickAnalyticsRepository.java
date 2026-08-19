package com.snipify.snipify.repo;

import com.snipify.snipify.model.ClickAnalytics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Integer> {

    @Query("SELECT COUNT(c) FROM ClickAnalytics c WHERE c.url.user.id= :userId")
    long countTotalClicksByUser(@Param("userId") long userId);

    @Query("SELECT COUNT(c) FROM ClickAnalytics c WHERE c.url.user.id= :userId AND c.clickedAt>= :time")
    long countClicksByTime(@Param("userId") long userId, @Param("time")LocalDateTime time);

    long countByUrl_urlId(long urlId);

    @Query("SELECT c.country ,Count(c) as total FROM ClickAnalytics c WHERE c.url.user.id= :userId GROUP BY c.country ORDER BY total DESC")
    List<Object[]> highestCountryByClicks(@Param("userId") long userId);


    @Query("SELECT c.country, COUNT(c) as total FROM ClickAnalytics c GROUP BY c.country ORDER BY total DESC")
    List<Object[]> highestCountryByClicksGlobal();

    Page<ClickAnalytics> findAllByUrl_urlId(long urlId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClickAnalytics c WHERE c.clickedAt>= :time")
    long totalClicksByTimeGlobal(@Param("time")LocalDateTime time);
}