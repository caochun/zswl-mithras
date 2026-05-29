package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yupengfei
 * @date 2024/5/15 11:43
 */
@AllArgsConstructor
@Getter
public enum LeaseOCRTypeEnum implements PullDown {

    LEASE_VAT_INVOICE("发票类型"),
    LEASE_VEHICLE_REGISTRATION("车证类型"),
    ;
    private final String display;

    @Override
    public String display() {
        return display;
    }
}
