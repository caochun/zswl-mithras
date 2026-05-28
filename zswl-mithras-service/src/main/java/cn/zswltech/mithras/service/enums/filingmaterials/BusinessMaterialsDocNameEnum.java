package cn.zswltech.mithras.service.enums.filingmaterials;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
/**
 * 资料归档-运营基础资料模板
 * @author lllin
 * @date 2025-12-04
 */
public enum BusinessMaterialsDocNameEnum {
    BUSINESS_LESSEE_BASIC("承租人基础资料清单.docx","归档-归档资料模板","基础资料清单", BusinessModuleEnum.FILING_BUSINESS_LESSEE_CLIENT.name()),
    BUSINESS_ENT_GUARANTOR_BASIC("法人保证人基础资料清单.docx","归档-归档资料模板","基础资料清单",BusinessModuleEnum.FILING_BUSINESS_ENT_CLIENT.name()),
    BUSINESS_IND_GUARANTOR_BASIC("自然人保证人基础资料清单.docx","归档-归档资料模板","基础资料清单",BusinessModuleEnum.FILING_BUSINESS_IND_CLIENT.name()),
    BUSINESS_CONTRACT("合同资料清单.docx","归档-归档资料模板","合同资料清单",BusinessModuleEnum.FILING_BUSINESS_PAYMENT.name()),
    BUSINESS_LEASEHOLD("租赁物资料清单.docx","归档-归档资料模板","租赁物资料清单",BusinessModuleEnum.FILING_BUSINESS_LEASEHOLD.name()),
    BUSINESS_COLLATERALIZATION("抵质押物资料清单.docx","归档-归档资料模板","抵质押物资料清单",BusinessModuleEnum.FILING_BUSINESS_COLLATERALIZATION.name()),
    BUSINESS_INNER_OPERATION("内部操作资料清单.docx","归档-归档资料模板","内部操作清单",BusinessModuleEnum.FILING_BUSINESS_INNER_OPERATION.name()),
    APPROVE_SNAPSHOT("审批快照.docx","归档-归档资料模板","审批快照",null),
    AFTER_OFFSITE("租后（非现场）管理资料清单.docx","归档-归档资料模板","租后（非现场）管理资料清单",null),
    AFTER_SITE("租后（现场）管理资料清单.docx","归档-归档资料模板","租后（现场）管理资料清单",null),
    OTHER_FILING("其他资料归档清单.docx","归档-归档资料模板","其他资料清单",null);
    ;


    BusinessMaterialsDocNameEnum(String display, String templateType, String docName,String relateDirCode) {
        this.display = display;
        this.templateType = templateType;
        this.docName  =docName;
        this.relateDirCode = relateDirCode;
    }

    public final String display;
    public final String templateType;
    public final String docName;
    public final String relateDirCode;

    public static BusinessMaterialsDocNameEnum of(String code) {
        for (BusinessMaterialsDocNameEnum value : BusinessMaterialsDocNameEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static BusinessMaterialsDocNameEnum getBusinessMaterialsDocNameEnumByRelateCode(String relateDirCode) {
        for (BusinessMaterialsDocNameEnum value : BusinessMaterialsDocNameEnum.values()) {
            if (value.getRelateDirCode().equals(relateDirCode)) {
                return value;
            }
        }
        return null;
    }

    public String getRelateDirCode() {
        return relateDirCode;
    }

    public String getDisplay() {
        return display;
    }

    public String getTemplateType() {
        return templateType;
    }

    public String getDocName() {
        return docName;
    }
}
