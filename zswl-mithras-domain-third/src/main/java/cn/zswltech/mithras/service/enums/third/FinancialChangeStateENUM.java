package cn.zswltech.mithras.service.enums.third;

import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FinancialChangeStateENUM {
    LPR_CHANGE("A", "LPR调整"),
    EARLY_REPAYMENT("B", "提前还款"),
    CHANGE_REPAY_PLAN("C","调整还款计划"),
    EXTENSION("D", "展期"),
    ContractEarlySettle("E", "提前结清"),
    ContractEarlySettle_Deductio("F", "提前结清抵扣"),
    ContractNormalSettle("G", "抵扣"),
        ;

    private String display;
    private String message;


    public static FinancialChangeStateENUM changeCqStatus(ProcessModelTypeEnum processModelTypeEnum, Integer deductio){
        switch (processModelTypeEnum){
            case ContractLPRChangeFlow:
                return FinancialChangeStateENUM.LPR_CHANGE;
            case ContractEarlyRepayFlow:
                return FinancialChangeStateENUM.EARLY_REPAYMENT;
            case ContractChangeRepayPlanFlow:
                return FinancialChangeStateENUM.CHANGE_REPAY_PLAN;
            case ContractExtensionFlow:
                return FinancialChangeStateENUM.EXTENSION;
            case ContractEarlySettleFlow:
                //提前结清抵扣
                if(YesOrNoNumberEnum.YES.getCode().equals(deductio)){
                    return FinancialChangeStateENUM.ContractEarlySettle_Deductio;
                }else {
                    return FinancialChangeStateENUM.ContractEarlySettle;
                }
            case ContractNormalSettleFlow:
                if(YesOrNoNumberEnum.YES.getCode().equals(deductio)){
                    return FinancialChangeStateENUM.ContractNormalSettle;
                }
            default:
                return null;
        }
    }

}
