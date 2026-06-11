package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 收集租赁，宝理，转让等二级类型枚举
 */
@Getter
public enum ContractBusinessModelEnum implements PullDown {
    hui_zu("回租"), zhi_zu("直租"), jyx_zu("经营性租赁"),
    yzmbl("有追明保理"), wzmbl("无追明保理"), yzabl("有追暗保理"),
    yz("有追"), wz("无追");

    ContractBusinessModelEnum(String display) {
        this.display = display;
    }

    public final String display;

    private static Map<String, ContractBusinessModelEnum> map;

    static {
        map = Stream.of(ContractBusinessModelEnum.values()).collect(Collectors.toMap(ContractBusinessModelEnum::name, e -> e, (a, b) -> a));
    }

    public static ContractBusinessModelEnum of(String bizType) {
        return map.get(bizType);
    }

    @Override
    public String display() {
        return display;
    }

    public static ContractBusinessModelEnum getBusinessModel(String leaseType, String factoringType, String zrType){
        ContractBusinessModelEnum contractBusinessModelEnum = map.get(leaseType);
        if(contractBusinessModelEnum != null) {
            return contractBusinessModelEnum;
        }
        contractBusinessModelEnum = map.get(factoringType);
        if(contractBusinessModelEnum != null) {
            return contractBusinessModelEnum;
        }
        return map.get(zrType);
    }
}
