package cn.zswltech.mithras.afterlease.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 租金催收首页卡片标签
 *
 * @author wangchuanhao
 * @date 2022/11/17 4:06 PM
 */
@AllArgsConstructor
@Getter
public enum RentCollectionIndexCardTag implements PullDown {

    DEDUCTION("减免"),
    OVERDUE("逾期"),
    NOTIFIED("已通知"),
    ;

    private String display;

    private static Map<String, RentCollectionIndexCardTag> map;

    static {
        map = Stream.of(RentCollectionIndexCardTag.values()).collect(Collectors.toMap(RentCollectionIndexCardTag::name, e -> e));
    }

    @Override
    public String display() {
        return display;
    }

    public static RentCollectionIndexCardTag of(String name) {
        return map.get(name);
    }
}
