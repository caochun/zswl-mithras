package cn.zswltech.mithras.fund.domain.enums.receiptrepay;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 批量界面
 *
 * @author wangchuanhao
 * @date 2022/9/21 12:48 AM
 */
@AllArgsConstructor
@Getter
public enum BatchFundReceiptRepayMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    DEFAULT("资料清单", 1),

    ;

    private final String display;

    /**
     * 排序优先级
     */
    private Integer sort;

    private static final Map<String, BatchFundReceiptRepayMaterialsEnum> map;

    static {
        map = Stream.of(BatchFundReceiptRepayMaterialsEnum.values()).collect(Collectors.toMap(BatchFundReceiptRepayMaterialsEnum::name, e -> e));
    }

    public static BatchFundReceiptRepayMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "BATCH_FUND_RECEIPT_REPAY";
    }

    @Override
    public String display() {
        return display;
    }

}
