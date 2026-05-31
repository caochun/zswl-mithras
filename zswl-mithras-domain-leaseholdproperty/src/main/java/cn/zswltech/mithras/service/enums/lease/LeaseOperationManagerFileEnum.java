package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaseOperationManagerFileEnum implements PullDown, IMaterialsTypeConvert {
    /**
     * 租赁物清单类型
     */
    OWNERSHIP_FILE("权属文件", 99),
    CHECK_DUPLICATE_FILE("查重文件", 88),
    OTHER_FILE("其他", 66);

    public final String display;
    public final Integer order;

    public static LeaseOperationManagerFileEnum of(String code) {
        for (LeaseOperationManagerFileEnum value : LeaseOperationManagerFileEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String businessModule() {
        return "LEASE_OPERATION_MANAGER";
    }

    @Override
    public String display() {
        return display;
    }
}
