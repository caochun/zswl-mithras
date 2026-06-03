package cn.zswltech.mithras.dashboard.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2024/6/19
 * @description
 */
@AllArgsConstructor
@Getter
public enum DashboardAfterLeaseCheckStatueEnum implements PullDown {
    NEW("待提交"),
    CHECKING("检查中"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
