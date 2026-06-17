package cn.zswltech.mithras.dashboard.report.catalog.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2024/12/21
 * @description
 */
@AllArgsConstructor
@Getter
public enum ManagementReportSourceEnum implements PullDown {
    GUAN_YUAN("观远BI"),
    MITHRAS("融租易后端");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
