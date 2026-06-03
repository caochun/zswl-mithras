package cn.zswltech.mithras.workbench.domain.enums;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/8 16:22
 */
public enum WorkbenchMetricDeptScope {
    /**
     * 租赁
     */
    ZSZL("租赁"),
    /**
     * 高端装备业务部
     */
    JSSYB("高端装备业务部"),
    /**
     * 化工建材业务部
     */
    HGJCYWB("化工建材业务部"),
    /**
     * 机械和加工业务部
     */
    JXHJGYWB("机械和加工业务部"),
    /**
     * 浙江业务部
     */
    JCSSYWB("浙江业务部"),
    /**
     * 冷链物流团队
     */
    LLWLTD("冷链物流团队"),
    /**
     * 智能制造业务部
     */
    XJZZHXJJTD("智能制造业务部"),
    /**
     * 交通物流业务部
     */
    JTYSYWB("交通物流业务部"),
    /**
     * 公用事业业务部
     */
    GGSY("公用事业业务部"),
    ;
    private String display;

    WorkbenchMetricDeptScope(String display) {
        this.display = display;
    }

    public String display() {
        return this.display;
    }
}
