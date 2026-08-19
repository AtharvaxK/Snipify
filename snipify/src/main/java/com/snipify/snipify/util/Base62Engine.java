package com.snipify.snipify.util;

public class Base62Engine {
    private static final String alphabet="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encode(Long n){
        if(n==0){
            return "";
        }
        int r= (int) (n%62);
        n=n/62;

        return encode(n)+alphabet.charAt(r);

    }

    public static long decode(String shortCode){

        if (shortCode == null || shortCode.isEmpty()){
            return 0L;
        }
        long result=0;
        for (int i = 0;i<shortCode.length(); i++) {
            char c = shortCode.charAt(i);
            int digit = alphabet.indexOf(c);
            result = result * 62 + digit; // Avoids Math.pow() double precision issues
        }
        return result;
    }


}
