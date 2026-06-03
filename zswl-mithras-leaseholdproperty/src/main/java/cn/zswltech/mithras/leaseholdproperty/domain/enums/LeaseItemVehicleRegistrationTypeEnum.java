package cn.zswltech.mithras.leaseholdproperty.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yupengfei
 * @date 2024/6/24 19:51
 */
@AllArgsConstructor
@Getter
public enum LeaseItemVehicleRegistrationTypeEnum {

    VEHICLE_REGISTRATION_CERTIFICATE("vehicle_registration_certificate","车证正本"),
    VEHICLE_REGIST_PAGE_MORTGAGE("vehicle_regist_page_mortgage","车证副本"),
            ;

    public final String fieldName;
    public final String display;
}
