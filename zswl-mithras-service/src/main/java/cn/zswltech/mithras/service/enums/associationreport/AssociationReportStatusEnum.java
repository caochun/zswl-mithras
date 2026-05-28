package cn.zswltech.mithras.service.enums.associationreport;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/4/21
 * @description
 */
@AllArgsConstructor
@Getter
public enum AssociationReportStatusEnum implements PullDown {
    WAIT("待报送"),
    SUCCESS("已报送"),
    FAILURE("报送失败")
    ;

    public final String display;

    @Override
    public String display() {
        return display;
    }

    public static AssociationReportStatusEnum of(String code) {
        for (AssociationReportStatusEnum value : AssociationReportStatusEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }


}
