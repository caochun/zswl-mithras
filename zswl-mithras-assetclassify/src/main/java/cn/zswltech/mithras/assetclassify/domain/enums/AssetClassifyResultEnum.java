package cn.zswltech.mithras.assetclassify.domain.enums;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@AllArgsConstructor
@Getter
public enum AssetClassifyResultEnum implements PullDown {
    NORMAL("正常", 1),
    ATTENTION("关注", 2),
    SECONDARY("次级", 3),
    SUSPICIOUS("可疑", 4),
    LOSS("损失", 5);

    private final String display;

    private final int order;

    private static final Map<String, AssetClassifyResultEnum> map = new HashMap<>();
    static {
        for (AssetClassifyResultEnum assetClassifyResultEnum : values()) {
            map.put(assetClassifyResultEnum.name(), assetClassifyResultEnum);
        }
    }

    public static AssetClassifyResultEnum of(String name) {
        return map.get(name);
    }

    public static AssetClassifyResultEnum getByOrder(String riskLevel) {
        for(AssetClassifyResultEnum assetClassifyResultEnum : AssetClassifyResultEnum.values()) {
            if (ObjectUtil.equals(Integer.valueOf(riskLevel), assetClassifyResultEnum.getOrder())) {
                return assetClassifyResultEnum;
            }
        }
        return null;
    }

    public static AssetClassifyResultEnum getByDisplay(String display) {
        for(AssetClassifyResultEnum assetClassifyResultEnum : AssetClassifyResultEnum.values()) {
            if (ObjectUtil.equals(display, assetClassifyResultEnum.getDisplay())) {
                return assetClassifyResultEnum;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }

    public static Set<String> lastThree() {
        Set<String> res = new HashSet<>();
        res.add(AssetClassifyResultEnum.SECONDARY.name());
        res.add(AssetClassifyResultEnum.SUSPICIOUS.name());
        res.add(AssetClassifyResultEnum.LOSS.name());
        return res;
    }
}
