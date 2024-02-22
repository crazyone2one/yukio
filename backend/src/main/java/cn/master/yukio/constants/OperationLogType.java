package cn.master.yukio.constants;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
public enum OperationLogType {
    ADD,
    DELETE,
    UPDATE,
    DEBUG,
    REVIEW,
    COPY,
    EXECUTE,
    SHARE,
    RESTORE,
    IMPORT,
    EXPORT,
    LOGIN,
    SELECT,
    RECOVER,
    LOGOUT,
    DISASSOCIATE,
    ASSOCIATE,
    ARCHIVED;

    public boolean contains(OperationLogType keyword) {
        return this.name().contains(keyword.name());
    }
}
