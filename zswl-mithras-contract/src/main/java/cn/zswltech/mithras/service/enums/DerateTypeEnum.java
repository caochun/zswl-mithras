package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2024/12/5
 * @description 减免方式
 */
@AllArgsConstructor
@Getter
public enum DerateTypeEnum implements PullDown {
    NONE("无减免"),
    ALL("全额减免"),
    PERCENT("百分比减免"),
    FIXED("固定金额减免");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
