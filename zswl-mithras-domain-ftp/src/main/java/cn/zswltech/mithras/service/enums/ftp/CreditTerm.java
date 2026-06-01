package cn.zswltech.mithras.service.enums.ftp;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 14:21
 */
public enum CreditTerm implements PullDown {
    // 一年期
    ONE_YEAR("1年期(含)以内"),
    //一到三年
    ONE_TO_THREE_YEARS("1-3年期(含)"),
    //三年以上
    MORE_THAN_THREE_YEARS("3年以上");

    private String display;

    CreditTerm(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
