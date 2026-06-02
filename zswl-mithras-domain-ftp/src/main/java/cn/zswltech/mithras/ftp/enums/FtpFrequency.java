package cn.zswltech.mithras.ftp.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum FtpFrequency implements PullDown {
    DAY("天"),MONTH("月"), SEASON("季"), HALF_YEAR("半年"), YEAR("年");

    FtpFrequency(String display) {
        this.display = display;
    }

    public final String display;

    public static FtpFrequency ofDisplay(String display) {
        for (FtpFrequency value : FtpFrequency.values()) {
            if (value.display.trim().equals(display)) {
                return value;
            }
        }
        return null;
    }

    public static FtpFrequency of(String name) {
        for (FtpFrequency value : FtpFrequency.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
