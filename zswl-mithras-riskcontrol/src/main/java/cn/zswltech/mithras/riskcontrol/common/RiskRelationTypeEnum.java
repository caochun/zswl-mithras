package cn.zswltech.mithras.riskcontrol.common;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * 关联关系类型枚举类(源自慧眼系统枚举值)
 */
public enum RiskRelationTypeEnum implements PullDown {

    JJZCG("基金重仓股"),
    GDLHYMX("股东链_慧眼模型"),
    GDLLPLBHBBBFW("股东链_披露被合并报表范围"),
    DWTZHYMX("对外投资链_慧眼模型"),
    DWTZLPLBHBBBFW("对外投资链_披露合并报表范围"),
    SKRHYMX("实控人_慧眼模型"),
    JTDDQY("集团顶点企业"),
    JTHXCY("集团核心成员"),
    JTQTCY("集团其他成员"),
    WGZGSTGDBDQY("为关注公司提供担保的企业"),
    BGZDXDBDQY("被关注对象担保的企业"),
    ZQFSR("证券发行人"),
    JGFXQ("机构发行券"),
    MINECT("我的补充穿透"),
    TFXRCT("同发行人穿透"),
    DBRCT("担保人穿透"),
    DXZS("对象自身"),
    QT("其他"),
    ;

    public String display;

    RiskRelationTypeEnum(String display) {
        this.display = display;
    }

    public static RiskRelationTypeEnum findByName(String name) {
        for (RiskRelationTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static RiskRelationTypeEnum findByDisplay(String display) {
        for (RiskRelationTypeEnum item : values()) {
            if (item.display.equals(display)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {return display;}
}
