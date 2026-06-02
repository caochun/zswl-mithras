package cn.zswltech.mithras.service.enums.contractcp;

/**
 * @create: 2022-08-20
 **/

public enum CashSelectTypeEnum {
    ALL("全部"),
    EARNEST_MONEY("保证金"),
    OTHERAMOUNT("服务费/咨询费/手续费"),
    EARLY_STOP_COMPENSATION("提前终止补偿金"),
    NOMINAL_PRICE("名义价款"),
    FIRST_RENT("首期租金");



    CashSelectTypeEnum(String display) {
        this.display = display;
    }
    public final String display;
}
