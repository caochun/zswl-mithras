package cn.zswltech.mithras.contract.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName TimeoutTypeEunm
 * @Description 超时类型
 * @Author jackerhe
 * @Date 2023/4/26 2:12 下午
 * @Version 1.0
 **/

@AllArgsConstructor
@Getter
public enum TimeoutTypeEnum {

    COLLECTION_RECORD_WARN("收款核销异常"),
    PAYMENT_RECORD_WARN("收款核销异常"),
    WRITE_OFF_WARN("核销异常"),
    CONTRACT_RENT("起租提醒");

    private final String display;

}
