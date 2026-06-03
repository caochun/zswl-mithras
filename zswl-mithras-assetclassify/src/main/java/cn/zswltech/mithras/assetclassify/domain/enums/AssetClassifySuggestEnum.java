package cn.zswltech.mithras.assetclassify.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@AllArgsConstructor
@Getter
public enum AssetClassifySuggestEnum implements PullDown {
    UNANIMITY("无异议", 0),
    NORMAL("正常", 1),
    ATTENTION("关注", 2),
    SECONDARY("次级", 3),
    SUSPICIOUS("可疑", 4),
    LOSS("损失", 5);

    private final String display;

    private final int order;

    private static final Map<String, AssetClassifySuggestEnum> map = new HashMap<>();
    static {
        for (AssetClassifySuggestEnum classifySuggestEnum : values()) {
            map.put(classifySuggestEnum.name(), classifySuggestEnum);
        }
    }
    public static AssetClassifySuggestEnum of (String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return this.display;
    }
}
