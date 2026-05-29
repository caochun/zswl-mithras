package cn.zswltech.mithras.service.enums.fund;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.Getter;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/23 15:28
 */
@Getter
public enum EnhanceCreditMethod implements PullDown {
    DB("担保"),
    XY("信用");
    private String display;

    EnhanceCreditMethod(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return this.display;
    }
}
