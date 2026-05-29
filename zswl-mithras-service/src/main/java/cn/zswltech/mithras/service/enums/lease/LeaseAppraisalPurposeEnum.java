package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


@AllArgsConstructor
@Getter
public enum LeaseAppraisalPurposeEnum implements PullDown {
    LEASE_ITEM_ASSESS("租赁物评估"),
    MORTGAGE_PLEDGE_ASSESS("抵质押评估"),
    OTHER("其他"),
    ;
    private final String display;

    public static LeaseAppraisalPurposeEnum of(String name) {
        for (LeaseAppraisalPurposeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
