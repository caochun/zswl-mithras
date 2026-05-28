package cn.zswltech.mithras.service.enums.riskcontrol.opinion;

import java.util.stream.Stream;

public enum RiskControlOpinionEnum {

    CHANGE_INFO("企业变更","CHANGE_INFO"),
    COURT_ANNOUNCE("法院公告","COURT_ANNOUNCE"),
    COURT_SESSION("开庭公告","COURT_SESSION"),
    CASE_INFO("立案信息","CASE_INFO"),
    OTHER("其他","OTHER"),
    ;

    public String lable;

    public String value;


    RiskControlOpinionEnum(String lable,String value) {
        this.lable = lable;
        this.value = value;
    }

    // 静态方法，根据code获取对应的UserStatus枚举常量
    public static RiskControlOpinionEnum getByLable(String lable) {
        for (RiskControlOpinionEnum status : values()) {
            if (status.lable.equals(lable)) {
                return status;
            }
        }
        // 如果没有匹配的枚举常量，你可以选择抛出异常或返回一个默认值，这取决于你的需求
        throw new IllegalArgumentException("No matching constant for code: " + lable);
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
