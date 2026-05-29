package cn.zswltech.mithras.service.enums.client;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/10/21
 * @description 法人客户资料大类
 */
@AllArgsConstructor
@Getter
public enum CorporationClientMaterialTypeEnum implements PullDown, IMaterialsTypeConvert {
    BASIC_INFORMATION("基础资料", "corporationClientBasicSubTypeEnum"),
    LEASE_APPLICATION("业务申请书", "corporationClientLeaseApplicationSubTypeEnum"),
    CREDIT_LETTER("征信授权书", "corporationClientCreditLetterSubTypeEnum"),
    FINANCIAL_INFORMATION("财务资料", "corporationClientFinancialSubTypeEnum"),
    BUSINESS_INFORMATION("经营资料", "corporationClientBusinessSubTypeEnum"),
    OTHERS("其他", "corporationClientOthersSubTypeEnum");

    private final String display;
    private final String subEnumName;

    @Override
    public String businessModule() {
        return BusinessModuleEnum.CLIENT.name();
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
                BASIC_INFORMATION.name(),
                FINANCIAL_INFORMATION.name(),
                BUSINESS_INFORMATION.name(),
                OTHERS.name()
        );
    }
}
