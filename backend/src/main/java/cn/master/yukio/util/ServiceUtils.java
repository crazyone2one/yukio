package cn.master.yukio.util;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
public class ServiceUtils {
    public static final int POS_STEP = 5000;

    /**
     * 保存资源名称，在处理 NOT_FOUND 异常时，拼接资源名称
     */
    private static final ThreadLocal<String> RESOURCE_NAME = new ThreadLocal<>();

    public static String getResourceName() {
        return RESOURCE_NAME.get();
    }

    public static void clearResourceName() {
        RESOURCE_NAME.remove();
    }
}
