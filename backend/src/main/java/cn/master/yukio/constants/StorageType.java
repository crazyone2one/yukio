package cn.master.yukio.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public enum StorageType {
    MINIO, GIT, LOCAL;

    public static boolean isGit(String storage) {
        return StringUtils.equalsIgnoreCase(GIT.name(), storage);
    }
}
