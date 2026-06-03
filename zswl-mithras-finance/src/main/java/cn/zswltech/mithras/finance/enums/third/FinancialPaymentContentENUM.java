package cn.zswltech.mithras.finance.enums.third;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.Getter;

@Getter
public enum FinancialPaymentContentENUM implements PullDown {
    KX01("材料款"),
    KX02("商品贸易款"),
    KX03("设备款"),
    KX04("购房款"),
    KX05("工程款"),
    KX06("劳务款"),
    KX07("服务费"),
    KX08("租赁款"),
    KX09("保证金"),
    KX0901("民工工资保证金"),
    KX0902("履约保证金"),
    KX0903("投标保证金"),
    KX0904("租赁保证金"),
    KX0905("安全保证金"),
    KX0906("工程保留金"),
    KX0999("其他保证金"),
    KX10("代垫应收款"),
    KX11("委托贷款"),
    KX12("出口退税款"),
    KX99("其他"),
    KX9901("押金"),
    KX9902("备用金"),
    KX9903("风险金"),
    KX9904("统筹外费用"),
    KX9999("其他");

    private final String display;

    FinancialPaymentContentENUM(String display) {
        this.display = display;
    }
    public static FinancialPaymentContentENUM findByName(String name) {
        for (FinancialPaymentContentENUM type : FinancialPaymentContentENUM.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
      return null;
    }

    @Override
    public String display() {
        return display + " " + name();
    }
}
