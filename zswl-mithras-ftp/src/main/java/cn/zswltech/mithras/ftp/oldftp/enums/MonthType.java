package cn.zswltech.mithras.ftp.oldftp.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 14:06
 */
public enum MonthType implements PullDown {
    // 孟月
    FIRST_MONTH("孟月"),
    // 仲月
    SECOND_MONTH("仲月"),
    // 季月
    THIRD_MONTH("季月"),
    // 均值
    MEAN_VALUE("均值");

    private String display;

    MonthType(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
