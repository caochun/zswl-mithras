package cn.zswltech.mithras.service.enums.contract;

import cn.hutool.core.collection.ListUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/7/29
 * @description 合同文本类型
 */
@AllArgsConstructor
@Getter
public enum ContractTextTypeEnum {
    STANDARD_TEXT(1, "标准合同文本"),
    NONSTANDARD_STANDARD_TEXT_CHANGE(0, "标准合同文本修改"),
    NONSTANDARD_TEXT(0, "非标准合同文本"),
    NONSTANDARD_OTHER(0, "运营部认定的其他情况")
    ;

    private final int standard;
    private final String display;

    public static List<ContractTextTypeEnum> listStandard() {
        return ListUtil.of(values()).stream().filter(e -> e.getStandard() == 1).collect(Collectors.toList());
    }

    public static List<ContractTextTypeEnum> listNonstandard() {
        return ListUtil.of(values()).stream().filter(e -> e.getStandard() == 0).collect(Collectors.toList());
    }
}
