package cn.zswltech.mithras.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @create: 2022-08-23
 **/
@Getter
public enum ImageTypeEnum {
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    BMP("bmp"),
    UNKNOWN("未知");
    public String display;
    private static Map<String, ImageTypeEnum> map;

    static {
        map = Stream.of(ImageTypeEnum.values()).collect(Collectors.toMap(ImageTypeEnum::getDisplay, e -> e));
    }
    ImageTypeEnum(String display){
        this.display = display;
    }

    public static ImageTypeEnum getByExName(String exName) {
        return map.getOrDefault(exName, UNKNOWN);
    }
}
