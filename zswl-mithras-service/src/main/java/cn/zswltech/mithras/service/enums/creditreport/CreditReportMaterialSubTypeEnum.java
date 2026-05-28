package cn.zswltech.mithras.service.enums.creditreport;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.client.CorporationClientMaterialTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/10/21
 * @description 征信报告小类
 */
@AllArgsConstructor
@Getter
public enum CreditReportMaterialSubTypeEnum implements IMaterialsTypeConvert {
    // 企业资料
    BUSINESS_LICENSE(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT, "营业执照复印件"),
    LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_ENTERPRISE(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT, "法人身份证复印件(正面)"),
    LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_ENTERPRISE(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT, "法人身份证复印件(反面)"),
    CREDIT_LETTER(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT, "征信授权书"),

    //经办人资料
    LEGAL_REPRESENTATIVE_ID_CARD_POSITIVE_HANDLER(CreditReportMaterialTypeEnum.HANDLER_CREDIT_REPORT, "法人身份证复印件(正面)"),
    LEGAL_REPRESENTATIVE_ID_CARD_NEGATIVE_HANDLER(CreditReportMaterialTypeEnum.HANDLER_CREDIT_REPORT, "法人身份证复印件(反面)"),
    ;


    private final CreditReportMaterialTypeEnum parentType;
    private final String display;

    public static List<CreditReportMaterialSubTypeEnum> listSub(CreditReportMaterialTypeEnum mainType) {
        List<CreditReportMaterialSubTypeEnum> result = new LinkedList<>();
        for (CreditReportMaterialSubTypeEnum item : values()) {
            if (item.parentType == mainType) {
                result.add(item);
            }
        }
        return result;
    }

    public String displayWithParent() {
        return getParentType().getDisplay() + "-" + display;
    }

    @Override
    public String businessModule() {
        return BusinessModuleEnum.CREDIT_REPORT_SELECT.name();
    }

    @Override
    public String display() {
        return this.display;
    }
}
