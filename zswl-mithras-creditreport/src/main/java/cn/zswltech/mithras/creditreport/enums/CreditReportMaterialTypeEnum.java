package cn.zswltech.mithras.creditreport.enums;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
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

    private static final String BUSINESS_MODULE = "CREDIT_REPORT_SELECT";

    private final String display;
    private final String subEnumName;

    @Override
    public String businessModule() {
        return BUSINESS_MODULE;
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
