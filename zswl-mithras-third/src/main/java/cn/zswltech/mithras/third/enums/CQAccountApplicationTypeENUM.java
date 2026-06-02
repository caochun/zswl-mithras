package cn.zswltech.mithras.third.enums;

import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import lombok.Getter;

/**
 * @ClassName CQBusinessTypeENUM
 * @Description 应收单-业务类型
 * @Author jackerhe
 * @Date 2024/5/16 11:35 上午
 * @Version 1.0
 **/
@Getter
public enum CQAccountApplicationTypeENUM {
    PROJECT_LEASE_START("项目起租"),
    PROJECT_CHANGE("项目变更"), // 包括租金表调整或提前结清等涉及到租金表的调整的都算
    ACCRUE_INTEREST_REMAINING_PRINCIPAL("计提利息（剩余本金法）"),
    ACCRUE_INTEREST_ACTUAL_RATE("计提利息（实际利率法）"),
    PROVISION("拨备"),
    ACCRUE_FINANCING_COST("计提融资成本"),
    ACCRUE_STAMP_DUTY("计提印花税");

    private final String display;

    CQAccountApplicationTypeENUM(String display) {
        this.display = display;
    }

    public static CQAccountApplicationTypeENUM getCqBusinessType(ProcessModelTypeEnum modelTypeEnum){
        if(modelTypeEnum == null){
            return null;
        }
        switch (modelTypeEnum){
            case ContractStartRentAutoFlow:
            case ContractStartRentFlow:
            case ContractAddNewReceiptFlow:
            case ContractAddNewReceiptAutoFlow:
                return PROJECT_LEASE_START;
            case ContractModifyFlow:
            case ContractEarlySettleFlow:
            case ContractNormalSettleFlow:
            case ContractLPRChangeFlow:
            case ContractExtensionFlow:
            case ContractEarlyRepayFlow:
            case ContractChangeRepayPlanFlow:
                return PROJECT_CHANGE;
            default:
               return null;
        }
    }

}
