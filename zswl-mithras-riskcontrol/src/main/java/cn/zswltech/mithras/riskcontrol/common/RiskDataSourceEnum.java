package cn.zswltech.mithras.riskcontrol.common;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * 慧眼数据枚举类
 */
public enum RiskDataSourceEnum implements PullDown {
    // 数据源
    FHC("金控"),
    XINSIGHT("慧眼"),
    MANUAL("手动"),
    // 预警星级/信号级
    /*MODULEID_40("40"),// 三星/红色
    MODULEID_41("41"),// 二星/黄色
    MODULEID_42("42"),// 二星/黄色*/
    // 来源类型
    MANUAL_ENTRY("人工录入"),
    AUTOMATIC_IMPORT("系统推送"),
    // 情感方向
    EMOTIONNAME_YJ("预警"),// 负面
    EMOTIONNAME_YB("一般"),// 中性
    EMOTIONNAME_ZM("正面"),// 正面
    ;

    public String display;

    RiskDataSourceEnum(String display) {
        this.display = display;
    }

    @Override
    public String display() {return display;}
}
