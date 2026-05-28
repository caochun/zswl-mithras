package cn.zswltech.mithras.service.enums.trackEvent;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishMaterialsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 跟踪事项文件类型
 */

@AllArgsConstructor
@Getter
public enum TrackEventMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    TRACK_EVENT_PROFILE("跟踪事项资料清单",1);

    private final String display;
    /**
     * 排序优先级
     */
    private Integer sort;

    private static final Map<String, TrackEventMaterialsEnum> map;

    static {
        map = Stream.of(TrackEventMaterialsEnum.values()).collect(Collectors.toMap(TrackEventMaterialsEnum::name, Function.identity(),(m1, m2)->m1));
    }

    public static TrackEventMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "TRACK_EVENT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
