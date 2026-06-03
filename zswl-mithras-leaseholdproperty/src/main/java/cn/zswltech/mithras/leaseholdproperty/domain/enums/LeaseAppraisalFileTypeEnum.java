package cn.zswltech.mithras.leaseholdproperty.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租赁物评估机构资料清单枚举
 */
@Getter
@AllArgsConstructor
public enum LeaseAppraisalFileTypeEnum implements PullDown, IMaterialsTypeConvert {

    APPRAISAL_DATA_LIST("资料清单", 1),
    ;

    public final String display;
    public final Integer order;

    public static LeaseAppraisalFileTypeEnum of(String code) {
        for (LeaseAppraisalFileTypeEnum value : LeaseAppraisalFileTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String businessModule() {
        return "LEASE_APPRAISAL_DATA_LIST";
    }

    @Override
    public String display() {
        return display;
    }
}
