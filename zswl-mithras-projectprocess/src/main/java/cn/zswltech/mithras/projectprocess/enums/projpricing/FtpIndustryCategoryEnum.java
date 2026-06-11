package cn.zswltech.mithras.projectprocess.enums.projpricing;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/2/26
 * @description
 */
@Getter
@AllArgsConstructor
public enum FtpIndustryCategoryEnum implements PullDown {
    FTP_PUBLIC_UTILITIES("公用事业类"),
    FTP_CIVIL_CONSUMPTION("民生消费类"),
    FTP_STATE_OWNED_INDUSTRY("国有产业类"),
    FTP_OTHER_INDUSTRY("其他产业类")
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static FtpIndustryCategoryEnum getByName(String name) {
        for (FtpIndustryCategoryEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static FtpIndustryCategoryEnum getByDisplay(String display) {
        for (FtpIndustryCategoryEnum item : values()) {
            if (item.getDisplay().equals(display)) {
                return item;
            }
        }
        return null;
    }
}
