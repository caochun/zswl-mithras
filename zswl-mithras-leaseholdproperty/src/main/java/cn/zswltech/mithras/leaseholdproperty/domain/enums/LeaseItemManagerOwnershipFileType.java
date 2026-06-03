package cn.zswltech.mithras.leaseholdproperty.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/9/26
 * @description
 */
@AllArgsConstructor
@Getter
public enum LeaseItemManagerOwnershipFileType implements PullDown {
    INVOICE("发票"),
    ORIGINAL_CONTRACT("原始合同"),
    CAR_REGISTER("车辆登记证"),
    FIXED_ASSET_LIST("固定资产清单"),
    TRANSFER_LETTER("划拨函"),
    IMMOVABLE_WARRANT("不动产权证"),
    OTHER("其他");

    private final String display;

    public static LeaseItemManagerOwnershipFileType of(String name) {
        for (LeaseItemManagerOwnershipFileType item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
