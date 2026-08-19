package com.snipify.snipify.util;

import com.snipify.snipify.Enums.Browsers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UrlUtil {

    public Browsers getBrowserFromHttpRequest(HttpServletRequest httpServletRequest){

        if (httpServletRequest == null) {
            return Browsers.OTHER;
        }
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (userAgent == null) {
            return Browsers.OTHER;
        }
        String browser=userAgent.toLowerCase();

        if (browser.contains("chrome")){
            return Browsers.CHROME;
        }

        else if (browser.contains("mozilla")){
            return Browsers.MOZILLA;
        } else if (browser.contains("firefox")) {
            return Browsers.FIREFOX;
        } else if (browser.contains("edg")) {
            return Browsers.EDGE;
        }
        else if(browser.contains("opr")){
            return Browsers.OPERA;
        }
        else if(browser.contains("safari")){
            return Browsers.SAFARI;
        }
        else {
            return Browsers.OTHER;
        }


    }

    public String getOSFromHttpRequest(HttpServletRequest httpServletRequest){
        if (httpServletRequest == null) {
            return "Other";
        }

        String header = httpServletRequest.getHeader("Sec-CH-UA-Platform");

        if (header == null || header.isBlank()) {
            return "Other";
        }
        String os = header.toLowerCase();

        if(os!=null){
            if(os.contains("windows")){
                return "Windows";
            } else if (os.contains("mac os x")) {
                return "macOs";
            }
            else if(os.contains("iphone")){
                return "iOS";
            } else if (os.contains("android")) {
                return "Android";
            } else if (os.contains("linux")){
                return "Linux";
            }
            else {
                return "other";
            }
        }
        else return null;
    }

    public String getIpFromHttpHeader(HttpServletRequest httpServletRequest){
        if (httpServletRequest==null){
            return null;
        }

        String IP=httpServletRequest.getHeader("X-Forwarded-For");

        if(IP!=null&&!IP.isBlank()){
            String[] ipArray=IP.split(",");
            if (!ipArray[0].isEmpty()&&!"unknown".equalsIgnoreCase(ipArray[0].trim())){
                return ipArray[0].trim();
            }
        }
        return httpServletRequest.getRemoteAddr();
    }


}
