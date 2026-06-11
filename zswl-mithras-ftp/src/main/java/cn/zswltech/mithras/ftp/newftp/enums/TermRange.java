package cn.zswltech.mithras.ftp.newftp.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 通用的期项范围枚举类
 * @author: zhaozhengkang
 * @date: 2023/5/19 11:16
 */
@Getter
@AllArgsConstructor
public enum TermRange implements PullDown {
    /**
     * 一年期
     */
    ONE_YEAR("1年期(含)以内", "1年"),
    /**
     * 一到三年
     */
    ONE_TO_THREE_YEARS("1-3年期(含)", "1-3年"),
    /**
     * 三到五年
     */
//    THREE_TO_FIVE_YEARS("3-5年期(含)", "3-5年"),
    /**
     * 三年以上
     */
    MORE_THAN_THREE_YEARS("3年以上", "3年以上"),
    /**
     * 五年以上
     */
//    MORE_THAN_FIVE_YEARS("5年以上", "5年以上"),
    ;

    private final String display;
    // 项目收益率审查意见书中的展示文案特殊处理
    private final String projReviewEarningRateDisplay;

    public static TermRange changeTermRange(Integer month) {
        if (month == null || month <= 12) {
            return ONE_YEAR;
        } else if (month <= 3 * 12) {
            return ONE_TO_THREE_YEARS;
        } else {
            return MORE_THAN_THREE_YEARS;
        }
    }

    public static TermRange ofName(String name){
        for (TermRange range : TermRange.values()) {
            if(range.name().equals(name)){
                return range;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
