package cn.zswltech.mithras.service.enums.associationreport;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@Getter
@AllArgsConstructor
public enum AssociationReportCategoryEnum implements PullDown {
    J0001("基本情况统计表", AssociationReportPeriodCategoryEnum.REALTIME, "V01", false),
    J0002("股东股权信息一览表-股东股权信息", AssociationReportPeriodCategoryEnum.REALTIME, "V01", false),
    J0003("股东股权信息一览表-股东变更记录", AssociationReportPeriodCategoryEnum.REALTIME, "V01", false),
    J0004("高管信息一览表", AssociationReportPeriodCategoryEnum.REALTIME, "V01", false),
    J0005("业务情况信息", AssociationReportPeriodCategoryEnum.MONTH, "V01", true),
    J0006("服务实体经济情况表", AssociationReportPeriodCategoryEnum.QUARTER, "V01", true),
    J0007("资产负债表", AssociationReportPeriodCategoryEnum.QUARTER, "V01", true),
    J0008("利润表", AssociationReportPeriodCategoryEnum.QUARTER, "V01", true),
    J0009("主要业务清单", AssociationReportPeriodCategoryEnum.MONTH, "V01", true),
    J0010("对外融资清单", AssociationReportPeriodCategoryEnum.QUARTER, "V01", true),
    J0011("最大十家客户（含集团）集中度统计信息", AssociationReportPeriodCategoryEnum.QUARTER, "V01", true),
    J0012("关联方信息汇总表", AssociationReportPeriodCategoryEnum.QUARTER, "V01", true),
    J0013("涉法涉讼涉访信息表", AssociationReportPeriodCategoryEnum.REALTIME, "V01", false),
    J0014("重大事项报告表-基本信息", AssociationReportPeriodCategoryEnum.REALTIME, "V01", false),
    J0015("重大事项报告表-重大事项报告情况", AssociationReportPeriodCategoryEnum.REALTIME, "V01", false),
    ;

    private final String display;
    private final AssociationReportPeriodCategoryEnum period;
    private final String version;
    private final boolean collectDataFromSystem;

    @Override
    public String display() {
        return display;
    }

    public static AssociationReportCategoryEnum findByName(String name) {
        for (AssociationReportCategoryEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static Set<AssociationReportCategoryEnum> allQuarterCategory() {
        Set<AssociationReportCategoryEnum> result = new HashSet<>();
        for (AssociationReportCategoryEnum item : values()) {
            if (item.getPeriod() == AssociationReportPeriodCategoryEnum.QUARTER) {
                result.add(item);
            }
        }
        return result;
    }
}
