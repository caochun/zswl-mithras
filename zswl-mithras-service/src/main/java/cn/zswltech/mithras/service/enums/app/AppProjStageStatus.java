package cn.zswltech.mithras.service.enums.app;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @author luyi
 */
public enum AppProjStageStatus implements PullDown {

    GROUP_CREDIT_ESTABLISH("集团授信立项"),
    GROUP_CREDIT_REVIEW("集团授信评审"),
    PROJ_ESTABLISH_BASE("项目立项"),
    PROJ_REVIEW_BASE("项目评审")
    ;

    AppProjStageStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static AppProjStageStatus of(String code) {
        for (AppProjStageStatus value : AppProjStageStatus.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
