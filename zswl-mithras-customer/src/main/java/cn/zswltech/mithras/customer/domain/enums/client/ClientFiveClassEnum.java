package cn.zswltech.mithras.customer.domain.enums.client;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
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
public enum ClientFiveClassEnum implements PullDown {

    NORMAL("1", "正常"),
    FOCUS("2", "关注"),
    SECONDARY("3", "次级"),
    SUSPICIOUS("4", "可疑"),
    LOSS("5", "损失"),
    OTHER("9", "未分类"),
    ;

    private String value;
    private String display;

    private static Map<String, ClientFiveClassEnum> map;

    static {
        map = Stream.of(ClientFiveClassEnum.values()).collect(Collectors.toMap(ClientFiveClassEnum::name, Function.identity(), (k1, k2)->k1));
    }

    public static ClientFiveClassEnum of(String name) {
        return map.get(name);
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
