package cn.zswltech.mithras.ftp.newftp.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;

import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/19 14:54
 */
public enum RiskIndustryClassify implements PullDown {
    /**
     * 其他产业类
     */
    INDUSTRY("其他产业类"),
    /**
     * 公用事业类
     */
    PUBLIC_UTILITY("公用事业类"),
    /**
     * 民生消费类
     */
    CIVIL_CONSUMPTION("民生消费类"),
    /**
     * 国有产业类
     */
    STATE_OWNED_INDUSTRY("国有产业类"),
//    /**
//     * 工程机械类（厂商担保模式）
//     */
//    ENGINEERING_MACHINERY("工程机械类（厂商担保模式）"),
    /**
     * 协同类租赁业务
     */
    COLLABORATIVE_LEASING_BUSINESS("协同类租赁业务"),
    /**
     * 协同类保理业务
     */
    COLLABORATIVE_FACTORING_BUSINESS("协同类保理业务"),
    ;
    private final String display;

    RiskIndustryClassify(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

    public static RiskIndustryClassify getByFtpIndustryCategory(FtpIndustryCategoryEnum ftpIndustryCategoryEnum) {
        if (Objects.isNull(ftpIndustryCategoryEnum)) {
            return null;
        }
        switch (ftpIndustryCategoryEnum) {
            case FTP_PUBLIC_UTILITIES: {
                return PUBLIC_UTILITY;
            }
            case FTP_CIVIL_CONSUMPTION: {
                return CIVIL_CONSUMPTION;
            }
            case FTP_STATE_OWNED_INDUSTRY: {
                return STATE_OWNED_INDUSTRY;
            }
            default: {
                return INDUSTRY;
            }
        }
    }
}
