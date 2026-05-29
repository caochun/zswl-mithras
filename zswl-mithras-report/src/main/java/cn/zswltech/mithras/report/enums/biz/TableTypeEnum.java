package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/4/6/14:24
 * @description
 */
@Getter
@AllArgsConstructor
public enum TableTypeEnum implements PullDown {
    /**
     * 表类型，数据修改时使用
     */
    ACCOUNT("账户表"),
    REPAY_PLAN("还款计划表"),
    ACTUAL_REPAY("实际还款表"),
    FIVE_CLASS("五级分类表"),
    SPECIAL("特定交易表"),
    OVERDUE("逾期表"),
    CLIENT("客户表"),
    GUARANTOR("保证表"),
    MORTGAGE("抵押表"),
    PLEDGE("质押表");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
