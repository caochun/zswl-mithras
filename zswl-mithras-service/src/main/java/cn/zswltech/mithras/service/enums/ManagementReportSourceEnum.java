package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.common.enums.PullDown;
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
