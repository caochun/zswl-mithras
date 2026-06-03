package cn.zswltech.mithras.assetclassify.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author: shaokang
 * @date: 2026/1/14
 **/
@AllArgsConstructor
@Getter
public enum AssetClassifyInitTypeEnum implements PullDown {
    QUARTER_END("季末初分"),
    QUARTER_MID("季中初分");

    private final String display;

    private static final Map<String, AssetClassifyInitTypeEnum> map = new HashMap<>();
    static {
        for (AssetClassifyInitTypeEnum assetClassifyStatusEnum : values()) {
            map.put(assetClassifyStatusEnum.name(), assetClassifyStatusEnum);
        }
    }
    public static AssetClassifyInitTypeEnum of (String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return this.display;
    }
}
