package cn.zswltech.mithras.document.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingqi
 * @date 2023/3/7
 * @description
 */
@Getter
public enum VideoTypeEnum {
    MP4("mp4"),
    WMV("wmv"),
    ASF("asf"),
    ASX("asx"),
    RM("rm"),
    RMVB("rmvb"),
    THREEGP("3gp"),
    MOV("mov"),
    M4V("m4v"),
    AVI("avi"),
    DAT("dat"),
    MKV("mkv"),
    FLV("flv"),
    VOB("vob"),
    UNKNOWN("未知");
    public final String display;
    private static Map<String, VideoTypeEnum> map;

    static {
        map = Stream.of(VideoTypeEnum.values()).collect(Collectors.toMap(VideoTypeEnum::getDisplay, e -> e));
    }
    VideoTypeEnum(String display){
        this.display = display;
    }

    public static VideoTypeEnum getByExName(String exName) {
        return map.getOrDefault(exName, UNKNOWN);
    }
}
