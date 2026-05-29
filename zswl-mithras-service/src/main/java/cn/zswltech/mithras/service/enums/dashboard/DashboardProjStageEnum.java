package cn.zswltech.mithras.service.enums.dashboard;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.enums.trackEvent.TrackTaskBizSourceEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DashboardProjStageEnum implements PullDown {
    PROJECT_STAGE_ESTABLISH("立项阶段"),
    PROJECT_STAGE_DUE_DILIGENCE("尽调阶段"),
    PROJECT_STAGE_REVIEW("评审阶段"),
    PROJECT_STAGE_PUT("投放阶段"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static DashboardProjStageEnum find(String name) {
        for (DashboardProjStageEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
