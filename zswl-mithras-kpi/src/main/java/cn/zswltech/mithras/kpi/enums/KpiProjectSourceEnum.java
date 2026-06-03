package cn.zswltech.mithras.kpi.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum KpiProjectSourceEnum implements PullDown {
    NEW("新增"),
    HISTORY("存量");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
