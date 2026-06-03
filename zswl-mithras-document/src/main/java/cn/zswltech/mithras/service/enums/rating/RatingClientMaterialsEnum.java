package cn.zswltech.mithras.service.enums.rating;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum RatingClientMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    RATING_CLIENT_SUPPLEMENT_FILE("补充说明资料");

    private final String display;

    private static final Map<String, RatingClientMaterialsEnum> map;

    static {
        map = Stream.of(RatingClientMaterialsEnum.values()).collect(Collectors.toMap(RatingClientMaterialsEnum::name, Function.identity(),(m1, m2)->m1));
    }

    public static RatingClientMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    @Override
    public String businessModule() {
        return "RATING_CLIENT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
