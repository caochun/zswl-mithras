package cn.zswltech.mithras.service.enums.payment;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.Getter;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/23 17:12
 */
@Getter
public enum PaymentWriteOffStatus implements PullDown {
    NO_PAID("未付款"),
    TO_BE_WRITE_OFF("待核销"),
    WRITTEN_OFF("核销完毕"),
    PART_WRITTEN_OFF("部分核销");
    public String display;
    PaymentWriteOffStatus(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
