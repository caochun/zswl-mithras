package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractTypeEnum implements PullDown, IMaterialsTypeConvert {
    MAIN_CONTRACT("主合同", 0),
    MAIN_CONTRACT_ATTACHMENT("主合同附件", 5),
    CONSULTING_CONTRACT("咨询合同", 10),
    GUARANTEE_CONTRACT("保证合同", 20),
    MORTGAGE_CONTRACT("抵押合同", 30),
    PLEDGE_CONTRACT("质押合同", 40),
    MORTGAGE_PLEDGE_FILE("抵质押文件", 50),
    RESOLUTION_FILE("决议文件", 60),
    LEASE_ITEM_FILE("租赁物文件", 70),
    OTHER_CONTRACT("其他", 80),
    BASE_PROFILE("基础资料", 90)
    ;

    private static final Map<String, ContractTypeEnum> map = new HashMap<>();

    static {
        for (ContractTypeEnum contractTypeEnum : values()) {
            map.put(contractTypeEnum.name(), contractTypeEnum);
        }
    }

    private final String display;
    private final Integer sort;

    public static ContractTypeEnum getByName(String name) {
        return map.get(name);
    }

    public static List<ContractTypeEnum> allEnum() {
        ContractTypeEnum[] types = values();
        return new LinkedList<>(Arrays.asList(types));
    }

    public static List<String> allName() {
        ContractTypeEnum[] types = values();
        List<String> all = new LinkedList<>();
        for (ContractTypeEnum contractTypeEnum : types) {
            all.add(contractTypeEnum.name());
        }
        return all;
    }

    @Override
    public String businessModule() {
        return "CONTRACT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
