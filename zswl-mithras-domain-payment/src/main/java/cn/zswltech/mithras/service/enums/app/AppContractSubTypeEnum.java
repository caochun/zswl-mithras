package cn.zswltech.mithras.service.enums.app;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.enums.payment.LendingMaterialType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
@Getter
@AllArgsConstructor
public enum AppContractSubTypeEnum implements IMaterialsTypeConvert {
    // 租赁-回租合同
    SIGN_LOCATION_PHOTO(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), "签约场地照片", 1),
    SIGN_VIDEO(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), "签约视频", 2),
    MAN_MACHINE_PHOTO(LendingMaterialType.LEASE_RELATED.name(), "人机合照", 3),
    DEVICE_PHOTO(LendingMaterialType.LEASE_RELATED.name(), "设备照片", 4),
    SIGN_PHOTO_VIDEO(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), "合同签署照片和视频", 5),
    ;

    private final String parent;
    private final String display;
    private final int sort;

    private static final Map<String, AppContractSubTypeEnum> map = new HashMap<>();

    static {
        for (AppContractSubTypeEnum contractSubTypeEnum : values()) {
            map.put(contractSubTypeEnum.name(), contractSubTypeEnum);
        }
    }

    public static AppContractSubTypeEnum getByName(String name) {
        return map.get(name);
    }

    @Override
    public String businessModule() {
        return "CONTRACT";
    }

    @Override
    public String display() {
        return display;
    }
}
