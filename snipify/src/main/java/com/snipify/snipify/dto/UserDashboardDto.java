package com.snipify.snipify.dto;

import lombok.Data;

import java.util.Map;

@Data
public class UserDashboardDto {

    private String Username;
    private String subdomain;
    private String email;
    private long totalUrls;
    private long totalClicksIn30day;
    private Map<String,Long> countryAnalytics;
}
