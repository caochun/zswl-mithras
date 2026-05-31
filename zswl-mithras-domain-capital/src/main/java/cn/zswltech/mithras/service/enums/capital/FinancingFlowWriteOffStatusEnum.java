package cn.zswltech.mithras.service.enums.capital;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/20/09:50
 * @description 资金流水特有
 */
@Getter
@AllArgsConstructor
public enum FinancingFlowWriteOffStatusEnum implements PullDown {
    PART_WRITE_OFF("部分核销", 2),
    NO_WRITE_OFF("未核销", 1),
    COMPLETE_WRITE_OFF("全部核销", 4),
    ;

    private final String display;
    private final Integer sort;

    @Override
    public String display() {
        return display;
    }

    public static FinancingFlowWriteOffStatusEnum of(String name){
        for (FinancingFlowWriteOffStatusEnum writeOffStatusEnum : FinancingFlowWriteOffStatusEnum.values()) {
            if(writeOffStatusEnum.name().equals(name)){
                return writeOffStatusEnum;
            }
        }
        return null;
    }
}
