package org.example.expensetrackerui.utils;

import java.util.prefs.Preferences;

public class JwtStorageUtil {
    private static final String JWT_KEY = "jwtToken";
    private static final Preferences prefs =
            Preferences.userNodeForPackage(JwtStorageUtil.class);

    public static void saveToken(String token){
        prefs.put(JWT_KEY,token);
    }

    public static String getToken(){
       return prefs.get(JWT_KEY,null);
    }

    public static void clearToken(){
        prefs.remove(JWT_KEY);
    }
}
