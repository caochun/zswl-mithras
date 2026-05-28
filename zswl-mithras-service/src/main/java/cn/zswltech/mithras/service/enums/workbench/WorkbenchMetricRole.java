package cn.zswltech.mithras.service.enums.workbench;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description: 工作台指标角色
 * @author: zhaozhengkang
 * @date: 2023/5/5 09:58
 */
public enum WorkbenchMetricRole implements PullDown {
    /**
     * 综合管理
     */
    COMPREHENSIVE_MANAGEMENT("综合管理", 1),
    /**
     * 项目经理
     */
    XMJL("项目经理", 3),
    /**
     * 高端装备业务部-业务部门主管
     */
    JSSYB_BDS("工作台-高端装备业务部主管", 2),
    /**
     * 化工建材业务部-业务部门主管
     */
    HGJCYWB_BDS("工作台-化工建材业务部主管", 2),
    /**
     * 机械和加工业务部-业务部门主管
     */
    JXHJGYWB_BDS("工作台-机械和加工业务部主管", 2),
    /**
     * 浙江业务部-业务部门主管
     */
    JCSSYWB_BDS("工作台-浙江业务部主管", 2),
    /**
     * 冷链物流团队-业务部门主管
     */
    LLWLTD_BDS("工作台-冷链物流团队主管", 2),
    /**
     * 智能制造业务部-业务部门主管
     */
    XJZZHXJJTD_BDS("工作台-智能制造业务部主管", 2),
    /**
     * 交通物流业务部-业务部门主管
     */
    JTYSYWB_BDS("工作台-交通物流业务部主管", 2),
    /**
     * 公用事业业务部-业务部门主管
     */
    GGSY_BDS("工作台-公用事业业务部主管", 2),
    ;
    private final String display;

    private final int sortCode;

    WorkbenchMetricRole(String display, int sortCode) {
        this.display = display;
        this.sortCode = sortCode;
    }

    @Override
    public String display() {
        return display;
    }

    public int sortCode() {
        return sortCode;
    }
}
