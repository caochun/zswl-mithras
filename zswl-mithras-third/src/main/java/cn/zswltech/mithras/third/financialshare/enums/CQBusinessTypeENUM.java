package cn.zswltech.mithras.third.financialshare.enums;

import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import lombok.Getter;

/**
 * @ClassName CQBusinessTypeENUM
 * @Description 应收单-业务类型
 * @Author jackerhe
 * @Date 2024/5/16 11:35 上午
 * @Version 1.0
 **/
@Getter
public enum CQBusinessTypeENUM {
    OTHER_BUSINESS("011", "其他业务", "租赁类型=经营性租赁"),
    FINANCE_LEASING("048", "融资租赁", "租赁类型=直租"),
    SUBLEASE_BUSINESS("050", "转租业务", "业务类型=转租赁、债权转让"),
    SALE_AND_LEASEBACK("051", "售后回租", "租赁类型=回租"),
    FACTORING_BUSINESS("052", "保理业务", "业务类型=保理");

    private final String code;
    private final String name;
    private final String cashFlowItem;

    CQBusinessTypeENUM(String code, String name, String cashFlowItem) {
        this.code = code;
        this.name = name;
        this.cashFlowItem = cashFlowItem;
    }

    public static CQBusinessTypeENUM getCqBusinessType(ProjectBizType bizType, LeaseType leaseType){
        if(bizType == null){
            return null;
        }
        switch (bizType){
            case ZL:
                if(LeaseType.hui_zu.equals(leaseType)){
                    return SALE_AND_LEASEBACK;
                } else if(LeaseType.zhi_zu.equals(leaseType)){
                    return FINANCE_LEASING;
                } else {
                    return OTHER_BUSINESS;
                }
            case ZZ:
                return SUBLEASE_BUSINESS;
            case BL:
                return FACTORING_BUSINESS;
            default:
                return null;
        }
    }

}
