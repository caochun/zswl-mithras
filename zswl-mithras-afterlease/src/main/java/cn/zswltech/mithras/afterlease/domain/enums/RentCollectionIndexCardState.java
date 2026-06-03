package cn.zswltech.mithras.afterlease.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 租金催收首页卡片状态
 *
 * @author wangchuanhao
 * @date 2022/11/17 4:06 PM
 */
@AllArgsConstructor
@Getter
public enum RentCollectionIndexCardState implements PullDown {

    PAID("已收款(绿色)"),
    PENDING("待收款（黄色，计划收款日期7天内）"),
    OVERDUE("已逾期（红色）"),
    NOT_YET_EXPIRED("未到期（灰色）"),
    ;

    private String display;

    private static Map<String, RentCollectionIndexCardState> map;

    static {
        map = Stream.of(RentCollectionIndexCardState.values()).collect(Collectors.toMap(RentCollectionIndexCardState::name, e -> e));
    }

    @Override
    public String display() {
        return display;
    }

    public static RentCollectionIndexCardState of(String name) {
        return map.get(name);
    }
}
