package cn.zswltech.mithras.third.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CQPaymentMethodENUM {
    JSFS01("现金", "现金"),
    JSFS02("现金支票", "支票"),
    JSFS03("转账支票", "支票"),
    JSFS04("电汇", "汇兑"),
    JSFS05("信汇", "汇兑"),
    JSFS06("票据", "商业承兑汇票"),
    JSFS07("银行承兑汇票", "银行承兑汇票"),
    JSFS08("信用证", "银行承兑汇票"),
    JSFS09("应收票据背书", "银行承兑汇票"),
    JSFS13("代扣代缴", "电子结算"),
    JSFS15("银企直联", "电子结算"),
    JSFS16("网银", "电子结算"),
    JSFS17("委托支付", "电子结算"),
    JSFS18("银行托收", "光票托收"),
    JSFS19("受托支付", "电子结算"),
    JSFS20("委托收款", "虚拟结算"),
    JSFS21("批量代发", "电子结算"),
    JSFS96("虚拟结算", "虚拟结算"),
    JSFS97("补单", "虚拟结算"),
    JSFS98("内部转移", "虚拟结算");

    private final String display;
    private final String category;

    CQPaymentMethodENUM(String display, String category) {
        this.display = display;
        this.category = category;
    }

    private static Map<String, CQPaymentMethodENUM> map;

    static {
        map = Stream.of(CQPaymentMethodENUM.values()).collect(Collectors.toMap(CQPaymentMethodENUM::getDisplay, e -> e, (a, b) -> a));
    }

    public String getDisplay() {
        return display;
    }

    public String getCategory() {
        return category;
    }

    public static CQPaymentMethodENUM ofDisplay(String display) {
        return map.get(display);
    }
}