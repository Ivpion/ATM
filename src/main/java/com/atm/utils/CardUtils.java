package com.atm.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class CardUtils {

    public static String generateCardPass(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++){
            sb.append(generateRandomNumber());
        }
        return sb.toString();
    }

    public static String generateCardNumber(){
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 20; i++){
            if (i % 5 == 0){
                sb.append(" ");
            } else {
                sb.append(generateRandomNumber());
            }
        }
        return sb.toString();
    }

    public static String cardPassEncoder(String passHash){
        return Hashing.sha256()
                .hashString(passHash, StandardCharsets.UTF_8)
                .toString();
    }

    public static boolean passChecker(String pass, String passHash){
        return cardPassEncoder(pass).equals(passHash);
    }


    private static String generateRandomNumber(){
      int random = (int) (Math.random() * 10);
      return String.valueOf(random);
    }
}
