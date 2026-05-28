package cn.zswltech.mithras.service.enums.creditreport;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 资产质量分类代码表
 * @author: jackerhe
 * @date: 2025/11/17 14:43
 **/
@AllArgsConstructor
@Getter
public enum CreditReportQualityClassificationEnum implements PullDown {

    NORMAL("正常类", 1),
    CONCERN("关注类", 2),
    NON_PERFORMING("不良类", 3),
    TOTAL("合计", 0);

    private final String display;

    private final Integer code;


    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportQualityClassificationEnum finaByName(String name) {
        for (CreditReportQualityClassificationEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportQualityClassificationEnum finaByDisplay(String display) {
        for (CreditReportQualityClassificationEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportQualityClassificationEnum finaByCode(Integer code) {
        for (CreditReportQualityClassificationEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }


}
