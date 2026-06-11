package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 五级分类枚举
 *
 * @author wangchuanhao
 * @date 2023/1/13 4:46 PM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crFiveClass")
public enum FiveClassEnum implements PullDown {

    NORMAL("1", "正常"),
    FOCUS("2", "关注"),
    SECONDARY("3", "次级"),
    SUSPICIOUS("4", "可疑"),
    LOSS("5", "损失"),
    OTHER("9", "未分类"),
    ;

    private String value;
    private String display;

    private static Map<String, FiveClassEnum> map;

    static {
        map = Stream.of(FiveClassEnum.values()).collect(Collectors.toMap(FiveClassEnum::name, Function.identity(), (k1, k2)->k1));
    }

    public static FiveClassEnum of(String name) {
        return map.get(name);
    }

    public static FiveClassEnum getByValue(String value) {
        for (FiveClassEnum fiveClassEnum : FiveClassEnum.values()) {
            if (fiveClassEnum.value.equals(value)) {
                return fiveClassEnum;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }

    @Override
    public String valueKey() {
        return value;
    }
}
