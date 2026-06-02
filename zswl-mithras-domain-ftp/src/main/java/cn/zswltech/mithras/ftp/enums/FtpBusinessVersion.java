package cn.zswltech.mithras.ftp.enums;

/**
 * @author dingqi
 * @date 2025/2/27
 * @description
 */
public enum FtpBusinessVersion {
    V2, V3;

    public static FtpBusinessVersion getLatestVersion() {
        return V3;
    }

    public static FtpBusinessVersion getByName(String name) {
        for (FtpBusinessVersion item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
