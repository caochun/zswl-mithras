package cn.zswltech.mithras.ftp.enums;

public enum FtpMonthlyInfoModule {

    /**
     * 指导主数据
     */
    GUIDANCE("指导主数据"),
    /**
     * 计价指导
     */
    VALUATION("计价指导"),
    /**
     * ftp定价指导
     */
    PRICING("ftp定价指导"),

    MATERIALS_LIST("资料清单"),
    ;

    FtpMonthlyInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static FtpMonthlyInfoModule of(String code) {
        for (FtpMonthlyInfoModule value : FtpMonthlyInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
