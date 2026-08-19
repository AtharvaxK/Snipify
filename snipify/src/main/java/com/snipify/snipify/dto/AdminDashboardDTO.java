package com.snipify.snipify.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class AdminDashboardDTO {
    private long totalUsers;
    private long totalUrls;
    private long totalClicks;
    private long totalClicksIn30Days;
    private Map<String ,Long> countryAnalytics;
}
