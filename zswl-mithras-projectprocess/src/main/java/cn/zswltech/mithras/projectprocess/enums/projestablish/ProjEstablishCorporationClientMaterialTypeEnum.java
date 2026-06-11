package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @create: 2022-07-27
 **/
public enum ProjEstablishCorporationClientMaterialTypeEnum implements PullDown, IMaterialsTypeConvert {
    BASIC_INFORMATION("基础信息", 1),
    LEASE_APPLICATION("业务申请书", 2),
    CREDIT_LETTER("征信授权书", 3),
    FINANCIAL_INFORMATION("财务资料", 4),
    BUSINESS_INFORMATION("经营资料", 5),
    OTHERS("其他", 99);

    ProjEstablishCorporationClientMaterialTypeEnum(String display, Integer order) {
        this.display = display;
        this.order = order;
    }

    public final String display;
    public final Integer order;

    public static ProjEstablishCorporationClientMaterialTypeEnum of(String code) {
        for (ProjEstablishCorporationClientMaterialTypeEnum value : ProjEstablishCorporationClientMaterialTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static ProjEstablishCorporationClientMaterialTypeEnum ofWithDefault(String code) {
        return Optional.ofNullable(of(code)).orElse(OTHERS);
    }

    @Override
    public String businessModule() {
        return "PROJ_ESTABLISH_CLIENT";
    }

    @Override
    public String display() {
        return display;
    }

    public static List<String> listClientDetailShow() {
        List<String> result = new LinkedList<>();
        for (ProjEstablishCorporationClientMaterialTypeEnum materialsType : values()) {
            result.add(materialsType.name());
        }
        return result;
    }
}
