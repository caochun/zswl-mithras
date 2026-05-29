package cn.zswltech.mithras.service.enums.payment;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
@AllArgsConstructor
public enum PaymentTypeEnum implements PullDown, IMaterialsTypeConvert {

    LOAN_REVIEW("放款审核", 0 );

    private static final Map<String, PaymentTypeEnum> map = new HashMap<>();

    static {
        for (PaymentTypeEnum contractTypeEnum : values()) {
            map.put(contractTypeEnum.name(), contractTypeEnum);
        }
    }

    private final String display;
    private final Integer sort;

    public static PaymentTypeEnum getByName(String name) {
        return map.get(name);
    }

    public static List<PaymentTypeEnum> allEnum() {
        PaymentTypeEnum[] types = values();
        return new LinkedList<>(Arrays.asList(types));
    }

    public static List<String> allName() {
        PaymentTypeEnum[] types = values();
        List<String> all = new LinkedList<>();
        for (PaymentTypeEnum contractTypeEnum : types) {
            all.add(contractTypeEnum.name());
        }
        return all;
    }

    @Override
    public String businessModule() {
        return "PAYMENT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
