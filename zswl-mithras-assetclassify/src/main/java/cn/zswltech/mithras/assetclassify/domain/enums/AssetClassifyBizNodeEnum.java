package cn.zswltech.mithras.assetclassify.domain.enums;

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
public enum AssetClassifyBizNodeEnum {
    INIT("初分", 1),
    REVIEW("复核",2),
    REVIEW_MEETING("评审会", 3),
    RISK_MEETING("风委会", 4),
    BOARD_MEETING("董事会", 5);

    private final String display;

    private final int order;

    private static final Map<String, AssetClassifyBizNodeEnum> map = new HashMap<>();
    static {
        for (AssetClassifyBizNodeEnum assetClassifyBizNodeEnum : values()) {
            map.put(assetClassifyBizNodeEnum.name(), assetClassifyBizNodeEnum);
        }
    }
    public static AssetClassifyBizNodeEnum of (String name) {
        return map.get(name);
    }

}
