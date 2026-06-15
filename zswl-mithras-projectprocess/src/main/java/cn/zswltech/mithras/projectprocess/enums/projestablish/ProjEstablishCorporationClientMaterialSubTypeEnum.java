package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/10/21
 * @description 法人客户资料小类
 */
@AllArgsConstructor
@Getter
public enum ProjEstablishCorporationClientMaterialSubTypeEnum implements IMaterialsTypeConvert {
    // 基础资料
    BUSINESS_LICENSE(ProjEstablishCorporationClientMaterialTypeEnum.BASIC_INFORMATION, "营业执照"),
    ARTICLES_OF_ASSOCIATION(ProjEstablishCorporationClientMaterialTypeEnum.BASIC_INFORMATION, "公司章程"),
    LICENCE_FOR_OPENING_ACCOUNTS(ProjEstablishCorporationClientMaterialTypeEnum.BASIC_INFORMATION, "开户许可证"),
    QUALIFICATION_PROOF(ProjEstablishCorporationClientMaterialTypeEnum.BASIC_INFORMATION, "资质证明"),
    LEGAL_REPRESENTATIVE_ID_CARD(ProjEstablishCorporationClientMaterialTypeEnum.BASIC_INFORMATION, "法定代表人身份证"),
    ENTERPRISE_PROFILE(ProjEstablishCorporationClientMaterialTypeEnum.BASIC_INFORMATION, "企业简介"),
    CREDIT_REPORT(ProjEstablishCorporationClientMaterialTypeEnum.BASIC_INFORMATION, "征信报告"),
    BASIC_INFORMATION_UNGROUPED(ProjEstablishCorporationClientMaterialTypeEnum.BASIC_INFORMATION, "未分组资料"),
    // 租赁业务申请书
    LEASE_APPLICATION(ProjEstablishCorporationClientMaterialTypeEnum.LEASE_APPLICATION, "业务申请书"),
    // 征信授权书
    CREDIT_LETTER(ProjEstablishCorporationClientMaterialTypeEnum.CREDIT_LETTER, "征信授权书"),
    // 财务资料
    FINANCIAL_REPORT(ProjEstablishCorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "财务报表"),
    FINANCIAL_TAX_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "财税资料"),
    BANK_FLOW(ProjEstablishCorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "银行流水"),
    FINANCING_DETAIL(ProjEstablishCorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "融资明细"),
    GUARANTEE_DETAIL(ProjEstablishCorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "担保明细"),
    BORROW_CONTRACT_CERTIFICATE(ProjEstablishCorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "借款合同及凭证"),
    FINANCIAL_INFORMATION_UNGROUPED(ProjEstablishCorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "未分组资料"),
    // 经营资料
    ENERGY_CONSUMPTION_CERTIFICATE(ProjEstablishCorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "能耗凭证"),
    PURCHASING_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "采购资料"),
    PRODUCTION_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "生产资料"),
    SALES_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "销售资料"),
    BUSINESS_INFORMATION_UNGROUPED(ProjEstablishCorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "未分组资料"),
    // 其他
    LEASE_ITEM_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "租赁物资料"),
    CAPITAL_VERIFICATION_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "验资材料"),
    OTHER_CREDIT_REPORT(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "其他征信报告"),
    ASSETS_CERTIFICATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "资产证明"),
    OWNERSHIP_CERTIFICATE(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "产权证明"),
    RELATED_ENTERPRISE_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "关联企业资料"),
    NEW_PROJECT_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "新建项目资料"),
    ZHIZU_SUPPLIER_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "直租供应商资料"),
    MORTGAGE_ITEM_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "抵押物资料"),
    GOV_FINANCIAL_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "政府财政资料"),
    OTHER_INFORMATION(ProjEstablishCorporationClientMaterialTypeEnum.OTHERS, "其他资料");


    private final ProjEstablishCorporationClientMaterialTypeEnum parentType;
    private final String display;

    public static List<ProjEstablishCorporationClientMaterialSubTypeEnum> listSub(ProjEstablishCorporationClientMaterialTypeEnum mainType) {
        List<ProjEstablishCorporationClientMaterialSubTypeEnum> result = new LinkedList<>();
        for (ProjEstablishCorporationClientMaterialSubTypeEnum item : values()) {
            if (item.parentType == mainType) {
                result.add(item);
            }
        }
        return result;
    }

    public String displayWithParent() {
        return getParentType().display() + "-" + display;
    }

    @Override
    public String businessModule() {
        return "PROJ_ESTABLISH_CLIENT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
