package cn.zswltech.mithras.report.enums.common;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
@Getter
@AllArgsConstructor
public enum ReportCacheEnum {

    /**
     * 征信报送审批序号 每天一个
     * 有效15天 防有问题
     */
    CREDIT_REPORT_SUBMIT_SEQ("credit_report_submit_seq:%s", 15 * 24 * 60 * 60 * 1000L),
    ;

    private final String code;
    /**
     * 毫秒
     */
    private final long expire;

    public static String getCacheKey(ReportCacheEnum reportCacheEnum, String suffix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mithras");
        stringBuilder.append(":");
        stringBuilder.append(reportCacheEnum.getCode());
        if (StrUtil.isNotBlank(suffix)) {
            stringBuilder.append(":");
            stringBuilder.append(suffix);
        }
        return stringBuilder.toString();
    }

    public String buildKey(Object... args) {
        return String.format("mithras:" + this.code, args);
    }
}
