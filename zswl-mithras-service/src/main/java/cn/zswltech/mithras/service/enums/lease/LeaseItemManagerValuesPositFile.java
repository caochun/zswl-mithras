package cn.zswltech.mithras.service.enums.lease;

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
public enum LeaseItemManagerValuesPositFile implements PullDown {
    INVOICE("发票"),
    FIXED_ASSET_LIST("固定资产清单"),
    EVALUATION_REPORT("评估报告"),
    OTHER("其他");

    private final String display;

    public static LeaseItemManagerValuesPositFile of(String name) {
        for (LeaseItemManagerValuesPositFile item : values()) {
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
