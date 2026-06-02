package cn.zswltech.mithras.ftp.enums;

public enum FtpQuarterlyInfoModule {

    /**
     * 指导主数据
     */
    GUIDANCE("指导主数据"),
    /**
     * 基本定价
     */
    BASE_PRICING("基本定价"),
    /**
     * 客户主体计价
     */
    CUSTOMER_PRICING("客户主体计价"),
    /**
     * 按月类型计价
     */
    MONTH_PRICING("按月类型计价"),
    /**
     * 按企业性质计价
     */
    ENTERPRISE_PRICING("按企业性质计价"),

    MATERIALS_LIST("资料清单"),
    ;

    FtpQuarterlyInfoModule(String display) {
        this.display = display;
    }

    public final String display;

    public static FtpQuarterlyInfoModule of(String code) {
        for (FtpQuarterlyInfoModule value : FtpQuarterlyInfoModule.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
