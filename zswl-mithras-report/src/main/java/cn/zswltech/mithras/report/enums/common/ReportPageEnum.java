package cn.zswltech.mithras.report.enums.common;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 征信报送页面
 *
 * @author wangchuanhao
 * @date 2022/10/8 11:01 AM
 */
@Getter
@AllArgsConstructor
public enum ReportPageEnum implements PullDown {
    CLIENT("客户表", true, 6),
    MORTGAGE("抵押表", true, 8),
    PLEDGE("质押表", true, 9),
    GUARANTOR("保证表", true, 7),
    ACCOUNT("账户表", true, 1),
    REPAY("还款表", false, 2),
    SPECIAL_TRADE("特定交易表", false, 3),
    OVERDUE_RECORD("逾期表", true, 4),
    FIVE_CLASS("五级分类表", false, 5);

    private final String display;
    private final boolean canEditReportFlag;
    private final int sort;

    @Override
    public String display() {
        return display;
    }

    public static ReportPageEnum find(String name) {
        for (ReportPageEnum value : ReportPageEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
