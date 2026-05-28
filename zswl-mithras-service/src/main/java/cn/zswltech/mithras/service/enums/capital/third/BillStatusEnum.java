package cn.zswltech.mithras.service.enums.capital.third;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/23/17:18
 * @description
 */
@Getter
@AllArgsConstructor
public enum BillStatusEnum {
    A("暂存"),
    B("已提交"),
    C("已审核"),
    ;

    private final String display;
}
