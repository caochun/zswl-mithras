package cn.zswltech.mithras.fund.enums.receiptrepay;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 资金收付款
 *
 * @author wangchuanhao
 * @date 2022/9/21 12:48 AM
 */
@AllArgsConstructor
@Getter
public enum FundReceiptRepayMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    DEFAULT("资料清单", 1),

    ;

    private final String display;

    /**
     * 排序优先级
     */
    private Integer sort;

    private static final Map<String, FundReceiptRepayMaterialsEnum> map;

    static {
        map = Stream.of(FundReceiptRepayMaterialsEnum.values()).collect(Collectors.toMap(FundReceiptRepayMaterialsEnum::name, e -> e));
    }

    public static FundReceiptRepayMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "FUND_RECEIPT_REPAY";
    }

    @Override
    public String display() {
        return display;
    }
}
