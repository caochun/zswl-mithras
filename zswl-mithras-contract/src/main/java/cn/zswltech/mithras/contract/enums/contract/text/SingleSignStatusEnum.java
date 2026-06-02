package cn.zswltech.mithras.contract.enums.contract.text;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/12/3 09:31
 * @description 单个签约人的签署状态
 */
@Getter
@AllArgsConstructor
public enum SingleSignStatusEnum implements PullDown {
    DRAFT("草稿"),
    SIGNING("签署中"),
    COMPLETE("已完成");

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static SingleSignStatusEnum ofName(String name) {
        for (SingleSignStatusEnum value : SingleSignStatusEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
