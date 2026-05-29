package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 合同变更类型 结清请去ContractAdvanceEnum
 *
 * @author wangchuanhao
 * @date 2022/8/23 11:04 AM
 */
public enum ContractChangeTypeEnum implements PullDown, IMaterialsTypeConvert {

    LPR_CHANGE("LPR调整"),

    EARLY_REPAYMENT("提前还款"),

    EXTENSION("展期"),

    CHANGE_REPAY_PLAN("调整还款计划"),

    OTHER("其他"),

    ;
    ContractChangeTypeEnum(String display) {
        this.display = display;
    }

    public final String display;

    private static Map<String, ContractChangeTypeEnum> map;

    static {
        map = Stream.of(ContractChangeTypeEnum.values()).collect(Collectors.toMap(ContractChangeTypeEnum::name, c -> c));
    }

    public static ContractChangeTypeEnum of (String name) {
        return map.get(name);
    }

    @Override
    public String businessModule() {
        return "CONTRACT";
    }

    @Override
    public String display() {
        return display;
    }
}
