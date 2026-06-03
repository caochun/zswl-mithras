package cn.zswltech.mithras.assetclassify.domain.enums;

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
public enum AssetClassifyMeetingFileMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    ASSET_CLASSIFY_REVIEW_MEETING("五级分类评审会会议材料", 1),
    ASSET_CLASSIFY_RISK_MEETING("五级分类风委会会议材料", 2),

    ;

    private final String display;
    private final int sort;

    private static Map<String, AssetClassifyMeetingFileMaterialsEnum> map;

    static {
        map = Stream.of(AssetClassifyMeetingFileMaterialsEnum.values()).collect(Collectors.toMap(AssetClassifyMeetingFileMaterialsEnum::name, e -> e));
    }

    @Override
    public String businessModule() {
        return "ASSET_CLASSIFY";
    }

    @Override
    public String display() {
        return this.display;
    }

    public static AssetClassifyMeetingFileMaterialsEnum of(String name) {
        return map.get(name);
    }

}
