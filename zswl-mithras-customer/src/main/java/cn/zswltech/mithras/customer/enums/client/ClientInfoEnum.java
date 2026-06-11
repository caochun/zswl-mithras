package cn.zswltech.mithras.customer.enums.client;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientInfoEnum implements PullDown {
    CORP_COMMERCE_INFO("工商信息"),
    CORP_ADDRESS_INFO("地址信息"),
    CORP_BANK_ACCOUNT("银行账户"),
    CORP_BOND_INFO("股票信息"),
    CORP_CONTACT_INFO("联系人"),
    CORP_RELATED_ENTERPRISE("关联企业"),
    CORP_SHAREHOLDER_INFO("股东信息");

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static ClientInfoEnum ofName(String name) {
        for (ClientInfoEnum ownershipTypeEnum : values()) {
            if (ownershipTypeEnum.name().equals(name)) {
                return ownershipTypeEnum;
            }
        }
        return null;
    }
}
