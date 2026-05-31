package cn.zswltech.mithras.service.enums.assetclassify;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
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
public enum AssetClassifyCheckMaterialsEnum implements PullDown, IMaterialsTypeConvert {
    CWBB_FRZXBG("最新一期财务报表、法人企业征信报告", 1),
    ZZSNSSBB("增值税纳税申报表", 2),
    ZRRZXBG("自然人征信报告", 3),
    SCBB("制造型企业提供生产报表", 4),
    GXZL("主体资格及章程变更等更新资料", 5),
    ZLW_XBZL("租赁物续保保险资料",6 ),
    ZDQKZL("反映重大情况的资料", 7),
    FLSS_QYXY("法律诉讼查询信息、企业信用查询信息", 8),
    OTHER("其他", 9);

    private final String display;
    private final int sort;

    private static Map<String, AssetClassifyCheckMaterialsEnum> map;

    static {
        map = Stream.of(AssetClassifyCheckMaterialsEnum.values()).collect(Collectors.toMap(AssetClassifyCheckMaterialsEnum::name, e -> e));
    }

    @Override
    public String businessModule() {
        return "ASSET_CLASSIFY_REVIEW";
    }

    @Override
    public String display() {
        return this.display;
    }

    public static AssetClassifyCheckMaterialsEnum of(String name) {
        return map.get(name);
    }

}
