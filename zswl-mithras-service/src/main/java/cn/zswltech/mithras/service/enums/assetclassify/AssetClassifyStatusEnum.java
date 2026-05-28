package cn.zswltech.mithras.service.enums.assetclassify;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: jackerhe
 * @date: 2023/1/4 4:51 下午
 **/
@AllArgsConstructor
@Getter
public enum AssetClassifyStatusEnum implements PullDown {
    FINISH("完成"),
    PROCESS("进行中"),
    WAIT("等待中");

    private final String display;

    private static final Map<String, AssetClassifyStatusEnum> map = new HashMap<>();

    static {
        for (AssetClassifyStatusEnum assetClassifyStatusEnum : values()) {
            map.put(assetClassifyStatusEnum.name(), assetClassifyStatusEnum);
        }
    }

    public static AssetClassifyStatusEnum of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return this.display;
    }
}
