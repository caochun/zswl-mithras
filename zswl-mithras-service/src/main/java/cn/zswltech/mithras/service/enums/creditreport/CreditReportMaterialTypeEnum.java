package cn.zswltech.mithras.service.enums.creditreport;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
@AllArgsConstructor
public enum CreditReportMaterialTypeEnum implements PullDown, IMaterialsTypeConvert{


    ENTERPRISE_CREDIT_REPORT("企业资料","enterpriseCreditReportSubTypeEnum"),
    HANDLER_CREDIT_REPORT("经办人资料", "handlerCreditReportSubTypeEnum"),
    CLIENT_CREDIT_REPORT("客户征信报告", "CLIENT_CREDIT_REPORT"),
    ;

    private final String display;
    private final String subEnumName;

    @Override
    public String businessModule() {
        return BusinessModuleEnum.CREDIT_REPORT_SELECT.name();
    }

    @Override
    public String display() {
        return this.display;
    }

    @Override
    public String childSelectName() {
        return subEnumName;
    }

    public static List<String> needCopyType() {
        return ListUtil.of(
                ENTERPRISE_CREDIT_REPORT.name(),
                HANDLER_CREDIT_REPORT.name()
        );
    }
}
