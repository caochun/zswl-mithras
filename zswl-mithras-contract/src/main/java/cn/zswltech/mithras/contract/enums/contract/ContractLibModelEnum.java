package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewInfoModule;

/**
 * @ClassName ContractLibModelEnum
 * @Description
 * @Author jackerhe
 * @Date 2022/8/23 7:18 下午
 * @Version 1.0
 **/
public enum ContractLibModelEnum {
    BASE_INFO("合同基本信息"),
    ZL_PRICE("合同租赁报价方案"),
    BL_PRICE("合同保理报价方案"),
    ZR_PRICE("合同债券转让报价方案"),
    RENT_ESTIMATE("概算租金表"),
    ACTUAL_ESTIMATE("实际租金表"),
    ACTUAL_ESTIMATE_ITEM("实际租金流量表"),
    TENANTRY("承租人"),
    // 银行账户一表多用，区分不同模块
    BANK_ACCOUNT_ZLSK("租赁收款账户"),
    BANK_ACCOUNT_ZZSK("转租赁收款账户"),
    BANK_ACCOUNT_BLSK("保理收款账户"),
    BANK_ACCOUNT_BLHK("保理回款账户"),
    BANK_ACCOUNT_ZRSK("债权转让收款账户"),
    BANK_ACCOUNT_ZRHK("债权转让回款账户"),
    LEASE_ITEM("租赁物清单"),
    GUARANTOR("担保措施"),
    MORTGAGE("抵押措施"),
    MORTGAGE_ITEM("抵押清单"),
    PLEDGE("质押措施"),
    PLEDGE_ITEM("质押清单"),
    CHANGE("合同变更"),
    PREPAYMENT("提前还款"),
    TEXT_INFO("合同文本类型"),


    CASH_FACTORING_PICE("现金流量明细"),
    MATERIALS_LIST("资料清单"),
    SETTLE_PLAN("结清方案")
    ;

    ContractLibModelEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static ContractLibModelEnum of(String code) {
        for (ContractLibModelEnum value : ContractLibModelEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
