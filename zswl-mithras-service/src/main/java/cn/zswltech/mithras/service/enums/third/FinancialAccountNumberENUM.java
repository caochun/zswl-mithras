package cn.zswltech.mithras.service.enums.third;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.Getter;

@Getter
public enum FinancialAccountNumberENUM implements PullDown {
    // 使用枚举常量的名称作为科目名称，并为其关联一个科目编码
    ACCOUNT_RECEIVABLE("应收账款", "1122"),
    ACCOUNT_RECEIVABLE_BILL("应收票据", "1121"),
    LONG_TERM_ACCOUNT_RECEIVABLE("长期应收款", "1531"),
    CONTRACT_ASSET("合同资产", "1462"),
    OTHER_ACCOUNT_RECEIVABLE("其他应收款", "1221"),
    PREPAID_ACCOUNT("预付账款", "1123");

    // 定义枚举的字段
    private final String name;
    private final String code;

    FinancialAccountNumberENUM(String name, String code) {
        this.name = name;
        this.code = code;
    }
    public static FinancialAccountNumberENUM findByName(String name) {
        for (FinancialAccountNumberENUM type : FinancialAccountNumberENUM.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
      return null;
    }

    @Override
    public String display() {
        return name + " " + code;
    }
}
