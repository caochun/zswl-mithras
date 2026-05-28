package cn.zswltech.mithras.service.enums.kpi;

import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author yangxiong
 * @date 2024/7/1/14:07
 * @description
 */
@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {
    PLATFORM("公用事业类"),
    PUBLIC("公共事业"),
    INDUSTRY("其他产业类"),
    FTP_CIVIL_CONSUMPTION("民生消费类"),
    FTP_STATE_OWNED_INDUSTRY("国有产业类"),
    TOTAL("合计"),
    DEPT_TOTAL("部门合计"),
    COMPANY_TOTAL("公司合计"),
    ALL_TOTAL("总合计"),
    ;


    private final String display;

    public static BusinessTypeEnum getByDisplay(String display) {
        for (BusinessTypeEnum businessTypeEnum : BusinessTypeEnum.values()) {
            if (businessTypeEnum.getDisplay().equals(display)) {
                return businessTypeEnum;
            }
        }
        return null;
    }

    public static BusinessTypeEnum ofName(String name) {
        for (BusinessTypeEnum businessTypeEnum : BusinessTypeEnum.values()) {
            if (businessTypeEnum.name().equals(name)) {
                return businessTypeEnum;
            }
        }
        return null;
    }

    /**
     * 枚举类型转换
     *
     * @param businessTypeEnum 源枚举
     * @return 目标枚举
     */
    public static FtpIndustryCategoryEnum transferEnum(BusinessTypeEnum businessTypeEnum) {
        if (Objects.nonNull(businessTypeEnum)) {
            for (FtpIndustryCategoryEnum ftpEnum : FtpIndustryCategoryEnum.values()) {
                if (businessTypeEnum.getDisplay().equals(ftpEnum.getDisplay())) {
                    return ftpEnum;
                }
            }
        }
        return null;
    }
}
