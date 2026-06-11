package cn.zswltech.mithras.third.financialshare.enums;

import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FinancialPaymentCodeENUM {
    BL_PAYMENT_CODE("FK09.021", "支付保理融资款本金"),
    ZL_PAYMENT_CODE("FK01.jr001", "投放租赁项目款"),
    ;

    private String display;
    private String name;


    public static FinancialPaymentCodeENUM changePaymentCode(String businessType){
        ProjectBizType projectBizType = ProjectBizType.of(businessType);
        switch (projectBizType){
            case BL:
                return FinancialPaymentCodeENUM.BL_PAYMENT_CODE;
            case ZL:
            case ZZ:
            case ZR:
            default:
                return FinancialPaymentCodeENUM.ZL_PAYMENT_CODE;
        }
    }

}
