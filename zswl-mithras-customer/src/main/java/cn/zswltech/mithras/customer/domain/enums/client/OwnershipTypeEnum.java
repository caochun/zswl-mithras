package cn.zswltech.mithras.customer.domain.enums.client;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OwnershipTypeEnum implements PullDown {
    DIRECT("直接控股"),
    INDIRECT("间接控股"),
    NON("非控股");

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static OwnershipTypeEnum ofName(String name) {
        for (OwnershipTypeEnum ownershipTypeEnum : values()) {
            if (ownershipTypeEnum.name().equals(name)) {
                return ownershipTypeEnum;
            }
        }
        return null;
    }
}
