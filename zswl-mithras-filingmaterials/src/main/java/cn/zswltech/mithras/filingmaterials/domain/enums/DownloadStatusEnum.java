package cn.zswltech.mithras.filingmaterials.domain.enums;

/**
 * 下载状态枚举
 */
public enum DownloadStatusEnum {

    IN_PROGRESS("下载中"),

    SUCCESS("已下载"),

    FAILED("下载失败"),
    ;

    DownloadStatusEnum(String display) {
        this.display = display;
    }

    private final String display;

    public String getDisplay() {
        return display;
    }

    public static String getDisplayByCode(String code){
        for (DownloadStatusEnum value : DownloadStatusEnum.values()) {
            if (value.name().equals(code)){
                return value.getDisplay();
            }
        }
        return null;
    }
}
