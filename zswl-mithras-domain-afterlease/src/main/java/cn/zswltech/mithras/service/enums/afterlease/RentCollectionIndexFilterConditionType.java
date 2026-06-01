package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 租金催收首页过滤枚举
 *
 * @author wangchuanhao
 * @date 2022/11/17 4:06 PM
 */
@AllArgsConstructor
@Getter
public enum RentCollectionIndexFilterConditionType implements PullDown {

    HIDE_FINISH("隐藏收款完成项"),
    NOT_NOTICE_YET("只看未通知"),
    OVERDUE("只看逾期"),
    ALL("显示全部"),
    ;

    private String display;

    private static Map<String, RentCollectionIndexFilterConditionType> map;

    static {
        map = Stream.of(RentCollectionIndexFilterConditionType.values()).collect(Collectors.toMap(RentCollectionIndexFilterConditionType::name, e -> e));
    }

    @Override
    public String display() {
        return display;
    }

    public static RentCollectionIndexFilterConditionType of(String name) {
        return map.getOrDefault(name, ALL);
    }
}
