package cn.zswltech.mithras.assetclassify.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 *  资产五级分类风险因子项目枚举
 * @author: shaokang
 * @date: 2025/12/29
 **/
@AllArgsConstructor
@Getter
public enum AssetClassifyClientRiskFactorEnum implements PullDown {
    OPERATION_LEASE("经营租赁"),
    NON_SHIPPING("非航运"),
    HISTORY("历史数据");

    private final String display;

    private static final Map<String, AssetClassifyClientRiskFactorEnum> map = new HashMap<>();

    static {
        for (AssetClassifyClientRiskFactorEnum assetClassifyClientRiskFactorEnum : values()) {
            map.put(assetClassifyClientRiskFactorEnum.name(), assetClassifyClientRiskFactorEnum);
        }
    }

    public static AssetClassifyClientRiskFactorEnum of (String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return this.display;
    }
}
