package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/5/18
 * @description
 */
@Getter
@AllArgsConstructor
public enum PricingApproveAuthEnum implements PullDown {
    bizDivisionLeader("业务分管领导", 2),
    priceCommittee("定价委员会", 0),
    chiefFinancialOfficer("财务总监", 1)
    ;

    private final String display;
    private final int level;

    @Override
    public String display() {
        return this.display;
    }

    public static PricingApproveAuthEnum find(String name) {
        for (PricingApproveAuthEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
