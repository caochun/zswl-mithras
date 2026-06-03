package cn.zswltech.mithras.leaseholdproperty.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2023/11/23/14:02
 * @description
 */
@Getter
@AllArgsConstructor
public enum LeaseOperationTypeEnum {
    /**
     * 操作类型
     */
    INSERT(1),
    REMOVE(2);

    private final Integer type;
}
