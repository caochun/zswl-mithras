package cn.zswltech.mithras.creditreport.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 相关还款责任类型代码表
 * @author: jackerhe
 * @date: 2025/11/17 14:43
 **/
@AllArgsConstructor
@Getter
public enum CreditReportFiveClassificationEnum implements PullDown {

    NORMAL(1, "正常"),
    ATTENTION(2, "关注"),
    SUBSTANDARD(3, "次级"),
    SUSPICIOUS(4, "可疑"),
    LOSS(5, "损失"),
    DEFAULT(6, "违约"),
    UNCLASSIFIED(9, "未分类");

    private final int code;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportFiveClassificationEnum finaByName(String name) {
        for (CreditReportFiveClassificationEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }


    public static CreditReportFiveClassificationEnum finaByCode(Integer code) {
        for (CreditReportFiveClassificationEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportFiveClassificationEnum finaByDisplay(String display) {
        for (CreditReportFiveClassificationEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }
}
