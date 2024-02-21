package cn.master.yukio.util;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
public class RedisKeyUtils {
    private static final String SPLIT = ":";
    private static final String PREFIX_LOGGED_USER = "user:logged";

    public static String getLoggedUserKey(String uid) {
        return PREFIX_LOGGED_USER + SPLIT + uid;
    }
}
