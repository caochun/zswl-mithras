package cn.zswltech.mithras.service.enums.financeoverdue;

import cn.zswltech.mithras.common.enums.PullDown;

public enum OverdueSettlementRelationEnum implements PullDown {
    REC_SETTLE("应收收款结算"),
    REC_SELF("收款红蓝对冲"),
    AR_SELF("应收红蓝对冲"),
    AR_TRANSFER("债权转移"),
    AR_AP_SETTLE("应收冲应付"),
    AR_WRITE_OFF("应收红蓝冲销"),
    BAD_DEBT_LOSS("坏账损失"),
    BAD_DEBT_RECOVERY("坏账收回"),
    REC_PAY_SETTLE("收款冲付款"),
    AR_PAY_SETTLE("应收退款结算"),
    AR_LIQ_SETTLE("未清项结算"),
    AP_AR_SETTLE("应付冲应收"),
    PAY_REC_SETTLE("付款冲收款"),
    AP_REC_SETTLE("应付退款结算"),
    REC_CLEARING("收款清理"),
    AR_TRANS_WAR("转出质保金");;

    OverdueSettlementRelationEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static OverdueSettlementRelationEnum of(String code) {
        for (OverdueSettlementRelationEnum value : OverdueSettlementRelationEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
