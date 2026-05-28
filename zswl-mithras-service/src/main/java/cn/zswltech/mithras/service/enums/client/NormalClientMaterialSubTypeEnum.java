package cn.zswltech.mithras.service.enums.client;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/10/21
 * @description 自然人客户资料小类
 */
@AllArgsConstructor
@Getter
public enum NormalClientMaterialSubTypeEnum implements IMaterialsTypeConvert {
    // 基础资料
    ID_CARD(NormalClientMaterialTypeEnum.BASIC_INFORMATION, "身份证"),
    MARRIAGE_CERT(NormalClientMaterialTypeEnum.BASIC_INFORMATION, "结婚证"),
    PERSONAL_CREDIT_REPORT(NormalClientMaterialTypeEnum.BASIC_INFORMATION, "个人征信报告"),
    ASSETS_CERTIFICATION(NormalClientMaterialTypeEnum.BASIC_INFORMATION, "资产证明"),
    BASIC_INFORMATION_UNGROUPED(NormalClientMaterialTypeEnum.BASIC_INFORMATION, "未分组资料"),
    // 其他
    LEASE_ITEM_INFORMATION(NormalClientMaterialTypeEnum.OTHERS, "租赁物资料"),
    CAPITAL_VERIFICATION_INFORMATION(NormalClientMaterialTypeEnum.OTHERS, "验资材料"),
    OTHER_CREDIT_REPORT(NormalClientMaterialTypeEnum.OTHERS, "其他征信报告"),
    OWNERSHIP_CERTIFICATE(NormalClientMaterialTypeEnum.OTHERS, "产权证明"),
    RELATED_ENTERPRISE_INFORMATION(NormalClientMaterialTypeEnum.OTHERS, "关联企业资料"),
    NEW_PROJECT_INFORMATION(NormalClientMaterialTypeEnum.OTHERS, "新建项目资料"),
    ZHIZU_SUPPLIER_INFORMATION(NormalClientMaterialTypeEnum.OTHERS, "直租供应商资料"),
    MORTGAGE_ITEM_INFORMATION(NormalClientMaterialTypeEnum.OTHERS, "抵押物资料"),
    GOV_FINANCIAL_INFORMATION(NormalClientMaterialTypeEnum.OTHERS, "政府财政资料"),
    OTHER_INFORMATION(NormalClientMaterialTypeEnum.OTHERS, "其他资料");

    private final NormalClientMaterialTypeEnum parentType;
    private final String display;

    public static List<NormalClientMaterialSubTypeEnum> listSub(NormalClientMaterialTypeEnum mainType) {
        List<NormalClientMaterialSubTypeEnum> result = new LinkedList<>();
        for (NormalClientMaterialSubTypeEnum item : values()) {
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
        return BusinessModuleEnum.CLIENT.name();
    }

    @Override
    public String display() {
        return this.display;
    }
}
