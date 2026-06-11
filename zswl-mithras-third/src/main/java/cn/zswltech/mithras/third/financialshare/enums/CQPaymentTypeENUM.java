package cn.zswltech.mithras.third.financialshare.enums;

import lombok.Getter;

/**
 * 苍穹系统 付款申请单，付款类型
 **/
@Getter
public enum CQPaymentTypeENUM {
    FK01_JR001("FK01.jr001", "投放租赁项目款", "投放款"),
    FK05_004("FK05.004", "退租赁保证金", "退保证金"),
    FK05_006("FK05.006", "退租赁质保金", "退质保金"),
    FK09_001("FK09.001", "支付短期借款本金", "归还融资到期本金"),
    FK09_002("FK09.002", "支付其他短期借款本金", "归还融资到期本金"),
    FK09_007("FK09.007", "支付长期借款本金", "归还融资到期本金"),
    FK09_011("FK09.011", "支付其他长期借款本金", "归还融资到期本金"),
    FK09_012("FK09.012", "支付公司债本金", "归还融资到期本金"),
    FK09_014("FK09.014", "支付中期票据本金", "归还融资到期本金"),
    FK08_004("FK08.004", "支付其他票据到期款", "归还融资到期本金"),
    FK08_005("FK08.005", "支付信用证到期款", "归还融资到期本金"),
    FK10_001("FK10.001", "支付短期借款利息", "支付融资到期利息"),
    FK10_004("FK10.004", "支付长期借款利息", "支付融资到期利息"),
    FK10_008("FK10.008", "支付其他长期借款利息", "支付融资到期利息"),
    FK10_009("FK10.009", "支付公司债利息", "支付融资到期利息"),
    FK10_011("FK10.011", "支付中期票据利息", "支付融资到期利息"),
    FK10_999("FK10.999", "支付其他利息支出", "支付融资到期利息"),
    FK01_999("FK01.999", "支付其他结算款", "支付融资费用项"),
    FK04_007("FK04.007", "支付其他保证金", "支付融资保证金")
    ;

    private final String code;
    private final String description;
    private final String adaptScene;


    CQPaymentTypeENUM(String code, String description, String adaptScene) {
        this.code = code;
        this.description = description;
        this.adaptScene = adaptScene;
    }

}