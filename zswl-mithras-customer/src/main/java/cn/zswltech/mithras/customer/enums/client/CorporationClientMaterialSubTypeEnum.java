package cn.zswltech.mithras.customer.enums.client;

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
public enum CorporationClientMaterialSubTypeEnum implements IMaterialsTypeConvert {
    // 基础资料
    BUSINESS_LICENSE(CorporationClientMaterialTypeEnum.BASIC_INFORMATION, "营业执照"),
    ARTICLES_OF_ASSOCIATION(CorporationClientMaterialTypeEnum.BASIC_INFORMATION, "公司章程"),
    LICENCE_FOR_OPENING_ACCOUNTS(CorporationClientMaterialTypeEnum.BASIC_INFORMATION, "开户许可证"),
    QUALIFICATION_PROOF(CorporationClientMaterialTypeEnum.BASIC_INFORMATION, "资质证明"),
    LEGAL_REPRESENTATIVE_ID_CARD(CorporationClientMaterialTypeEnum.BASIC_INFORMATION, "法定代表人身份证"),
    ENTERPRISE_PROFILE(CorporationClientMaterialTypeEnum.BASIC_INFORMATION, "企业简介"),
    CREDIT_REPORT(CorporationClientMaterialTypeEnum.BASIC_INFORMATION, "征信报告"),
    BASIC_INFORMATION_UNGROUPED(CorporationClientMaterialTypeEnum.BASIC_INFORMATION, "未分组资料"),
    // 租赁业务申请书
    LEASE_APPLICATION(CorporationClientMaterialTypeEnum.LEASE_APPLICATION, "业务申请书"),
    // 征信授权书
    CREDIT_LETTER(CorporationClientMaterialTypeEnum.CREDIT_LETTER, "征信授权书"),
    // 财务资料
    FINANCIAL_REPORT(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "财务报表"),
    FINANCIAL_TAX_INFORMATION(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "财税资料"),
    BANK_FLOW(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "银行流水"),
    FINANCING_DETAIL(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "融资明细"),
    GUARANTEE_DETAIL(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "担保明细"),
    BORROW_CONTRACT_CERTIFICATE(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "借款合同及凭证"),
    FINANCIAL_INFORMATION_UNGROUPED(CorporationClientMaterialTypeEnum.FINANCIAL_INFORMATION, "未分组资料"),
    // 经营资料
    ENERGY_CONSUMPTION_CERTIFICATE(CorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "能耗凭证"),
    PURCHASING_INFORMATION(CorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "采购资料"),
    PRODUCTION_INFORMATION(CorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "生产资料"),
    SALES_INFORMATION(CorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "销售资料"),
    BUSINESS_INFORMATION_UNGROUPED(CorporationClientMaterialTypeEnum.BUSINESS_INFORMATION, "未分组资料"),
    // 其他
    LEASE_ITEM_INFORMATION(CorporationClientMaterialTypeEnum.OTHERS, "租赁物资料"),
    CAPITAL_VERIFICATION_INFORMATION(CorporationClientMaterialTypeEnum.OTHERS, "验资材料"),
    OTHER_CREDIT_REPORT(CorporationClientMaterialTypeEnum.OTHERS, "其他征信报告"),
    ASSETS_CERTIFICATION(CorporationClientMaterialTypeEnum.OTHERS, "资产证明"),
    OWNERSHIP_CERTIFICATE(CorporationClientMaterialTypeEnum.OTHERS, "产权证明"),
    RELATED_ENTERPRISE_INFORMATION(CorporationClientMaterialTypeEnum.OTHERS, "关联企业资料"),
    NEW_PROJECT_INFORMATION(CorporationClientMaterialTypeEnum.OTHERS, "新建项目资料"),
    ZHIZU_SUPPLIER_INFORMATION(CorporationClientMaterialTypeEnum.OTHERS, "直租供应商资料"),
    MORTGAGE_ITEM_INFORMATION(CorporationClientMaterialTypeEnum.OTHERS, "抵押物资料"),
    GOV_FINANCIAL_INFORMATION(CorporationClientMaterialTypeEnum.OTHERS, "政府财政资料"),
    OTHER_INFORMATION(CorporationClientMaterialTypeEnum.OTHERS, "其他资料");


    private final CorporationClientMaterialTypeEnum parentType;
    private final String display;

    public static List<CorporationClientMaterialSubTypeEnum> listSub(CorporationClientMaterialTypeEnum mainType) {
        List<CorporationClientMaterialSubTypeEnum> result = new LinkedList<>();
        for (CorporationClientMaterialSubTypeEnum item : values()) {
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
        return "CLIENT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
