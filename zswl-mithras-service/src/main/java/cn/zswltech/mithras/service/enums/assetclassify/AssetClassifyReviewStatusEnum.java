package cn.zswltech.mithras.service.enums.assetclassify;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/4 4:51 下午
 **/
@AllArgsConstructor
@Getter
public enum AssetClassifyReviewStatusEnum implements PullDown {
    FINISH("已复核"),
    PROCESS("未复核");

    private final String display;

    private static final Map<String, AssetClassifyReviewStatusEnum> map = new HashMap<>();
    static {
        for (AssetClassifyReviewStatusEnum assetClassifyStatusEnum : values()) {
            map.put(assetClassifyStatusEnum.name(), assetClassifyStatusEnum);
        }
    }
    public static AssetClassifyReviewStatusEnum of (String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return this.display;
    }
}
