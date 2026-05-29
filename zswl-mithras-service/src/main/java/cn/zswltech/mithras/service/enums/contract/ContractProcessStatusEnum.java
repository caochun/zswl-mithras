package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dingqi
 * @date 2022/8/25
 * @description 合同审批状态枚举
 */
@AllArgsConstructor
@Getter
public enum ContractProcessStatusEnum implements PullDown {
    NEW_UNCOMMIT("新建未提交"),
    NEW_COMMIT("新建审批中"),
    NEW_CANCEL("取消新建"),
    NEW_PASS("新建审批通过"),

    START_RENT_UNCOMMIT("起租未提交"),
    START_RENT_COMMIT("起租审批中"),
    START_RENT_CANCEL("取消起租"),
    START_RENT_PASS("起租审批通过"),

    NEW_RECEIPT_UNCOMMIT("新增借据(投放)未提交"),
    NEW_RECEIPT_COMMIT("新增借据(投放)审批中"),
    NEW_RECEIPT_CANCEL("取消新增借据(投放)"),
    NEW_RECEIPT_PASS("新增借据(投放)审批通过"),

    CHANGE_UNCOMMIT("变更未提交"),
    CHANGE_COMMIT("变更审批中"),
    CHANGE_CANCEL("取消变更"),
    CHANGE_PASS("变更审批通过"),

    RETREAT_UNCOMIIT("保证金退抵未提交"),
    RETREAT_COMMIT("保证金退抵审批中"),
    RETREAT_CANCEL("取消保证金退抵"),
    RETREAT_PASS("保证金退抵审批通过"),
    RETREAT_REJECT("保证金退抵审批不通过"),

    SETTLE_UNCOMIIT("结清未提交"),
    SETTLE_COMMIT("结清审批中"),
    SETTLE_CANCEL("取消结清"),
    SETTLE_PASS("结清审批通过");



    public final String display;

    @Override
    public String display() {
        return display;
    }

    public static ContractProcessStatusEnum of(String code) {
        for (ContractProcessStatusEnum value : ContractProcessStatusEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static Set<String> canDoStatus() {
        Set<String> canDoStatus = new HashSet<>();
        canDoStatus.add(NEW_PASS.name());
        canDoStatus.add(NEW_RECEIPT_CANCEL.name());
        canDoStatus.add(NEW_RECEIPT_PASS.name());
        canDoStatus.add(START_RENT_CANCEL.name());
        canDoStatus.add(START_RENT_PASS.name());
        canDoStatus.add(CHANGE_CANCEL.name());
        canDoStatus.add(CHANGE_PASS.name());
        canDoStatus.add(SETTLE_CANCEL.name());
        canDoStatus.add(RETREAT_CANCEL.name());
        canDoStatus.add(RETREAT_PASS.name());
        canDoStatus.add(RETREAT_REJECT.name());
        return canDoStatus;
    }

    public static Set<String> canStartRentStatus() {
        Set<String> canDoStatus = new HashSet<>();
        canDoStatus.add(NEW_PASS.name());
        canDoStatus.add(NEW_RECEIPT_CANCEL.name());
        canDoStatus.add(NEW_RECEIPT_PASS.name());
        canDoStatus.add(CHANGE_CANCEL.name());
        canDoStatus.add(CHANGE_PASS.name());
        canDoStatus.add(START_RENT_CANCEL.name());
        canDoStatus.add(RETREAT_CANCEL.name());
        canDoStatus.add(RETREAT_PASS.name());
        canDoStatus.add(RETREAT_REJECT.name());
        return canDoStatus;
    }

    public static Set<String> canNewReceiptStatus() {
        Set<String> canDoStatus = canStartRentStatus();
        canDoStatus.add(START_RENT_PASS.name());
        return canDoStatus;
    }
}
