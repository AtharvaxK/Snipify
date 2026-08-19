package com.snipify.snipify.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.snipify.snipify.Enums.Browsers;
import com.snipify.snipify.model.ClickAnalytics;
import com.snipify.snipify.repo.ClickAnalyticsRepository;
import com.snipify.snipify.repo.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class LogAnalyticsAsyncService {
    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final UrlRepository urlRepository;
    private final DatabaseReader databaseReader;


    @Async("taskExecutor")
    public void logAnalyticsAsync(Long urlId, Browsers browser, String device, LocalDateTime clickedAt,String ip)  {

        String countryName = "Unknown";
        String cityName = "Unknown";

        try {

            if (ip != null && !ip.isBlank()) {
                InetAddress ipAddress = InetAddress.getByName("103.211.216.1    ");


                CityResponse response = databaseReader.city(ipAddress);

                Country country = response.country();
                City city = response.city();


                if (country != null && country.name() != null) {
                    countryName = country.name();
                }
                if (city != null && city.name() != null) {
                    cityName = city.name();
                }
            }
        }
        catch (Exception e){
                log.warn("Could not resolve location for IP '{}': {}", ip, e.getMessage());
        }

        try {
            log.info("Processing analytics on thread: {}", Thread.currentThread().getName());
            ClickAnalytics clickAnalytics=new ClickAnalytics();
            clickAnalytics.setUrl(urlRepository.getReferenceById(urlId));
            clickAnalytics.setBrowser(browser);
            clickAnalytics.setDevice(device);
            clickAnalytics.setClickedAt(clickedAt);
            clickAnalytics.setCity(cityName);
            clickAnalytics.setCountry(countryName);
            clickAnalyticsRepository.save(clickAnalytics);
            log.info("Click analytics saved successfully for urlId: {}", urlId);
        }
        catch (Exception e){
            log.error("Failed to save async analytics for urlId: {}", urlId, e);
        }


    }

}
