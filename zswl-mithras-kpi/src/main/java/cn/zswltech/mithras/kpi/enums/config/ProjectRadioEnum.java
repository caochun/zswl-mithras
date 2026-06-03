package cn.zswltech.mithras.kpi.enums.config;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description 项目提奖比例参数值枚举
 */
@Getter
@AllArgsConstructor
public enum ProjectRadioEnum implements PullDown {
    INDUSTRY_HISTORY("INDUSTRY", "HISTORY", "产业类-存量"),
    INDUSTRY_NEW("INDUSTRY", "NEW", "产业类-新增"),

    PUBLIC_HISTORY("PUBLIC", "HISTORY", "公共事业类-存量"),
    PUBLIC_NEW("PUBLIC", "NEW", "公共事业类-新增"),

    FACTORY_HISTORY("FACTORY", "HISTORY", "厂商类-存量"),
    FACTORY_NEW("FACTORY", "NEW", "厂商类-新增");

    private final String projectType;
    private final String projectSource;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
