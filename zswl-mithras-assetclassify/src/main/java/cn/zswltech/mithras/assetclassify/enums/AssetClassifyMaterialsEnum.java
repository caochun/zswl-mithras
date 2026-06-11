package cn.zswltech.mithras.assetclassify.enums;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 */
@AllArgsConstructor
@Getter
public enum AssetClassifyMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    ASSET_CLASSIFY_REVIEW("五级分类复核审批材料", 1),
    ASSET_CLASSIFY_CHECK_REPORT("五级分类检查报告材料", 2),
    ASSET_CLASSIFY_SUMMARY("租赁资产五级分类认定汇总审批表", 3)

    ;

    private final String display;
    private final int sort;

    private static Map<String, AssetClassifyMaterialsEnum> map;

    static {
        map = Stream.of(AssetClassifyMaterialsEnum.values()).collect(Collectors.toMap(AssetClassifyMaterialsEnum::name, e -> e));
    }

    @Override
    public String businessModule() {
        return "ASSET_CLASSIFY_REVIEW";
    }

    @Override
    public String display() {
        return this.display;
    }

    public static AssetClassifyMaterialsEnum of(String name) {
        return map.get(name);
    }

}
