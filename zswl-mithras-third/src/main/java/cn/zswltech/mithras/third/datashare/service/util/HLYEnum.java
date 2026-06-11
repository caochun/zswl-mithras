package cn.zswltech.mithras.third.datashare.service.util;

import cn.zswltech.mithras.foundation.metadata.PullDown;

public enum HLYEnum implements PullDown {

    TOKEN("/oauth/token", "获取动态Token"),
    ARCHIVES("/api/open/expenseReport/archives", "报销单单号查询"),
    AUDIT_RESULT_DETAIL("/api/open/expenseReport/auto/audit/result/detail", "查询智能审核/AI坐席判断结果"),
    AUDIT_RESULT_LIST("/api/open/expenseReport/auto/audit/result/list", " 查询智能审核/AI坐席判断结果列表"),
    ;

    HLYEnum(String url, String operate) {
        this.url = url;
        this.operate = operate;
    }

    public final String url;
    public final String operate;

    @Override
    public String display() {
        return url;
    }
}
