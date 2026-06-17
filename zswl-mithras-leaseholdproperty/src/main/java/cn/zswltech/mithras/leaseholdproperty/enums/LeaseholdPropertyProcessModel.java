package cn.zswltech.mithras.leaseholdproperty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum LeaseholdPropertyProcessModel {

    LEASE_CREATE("LeaseCreateFlow", "租赁物创建"),
    LEASE_MODIFY("LeaseModifyFlow", "租赁物变更"),
    APPRAISAL_COMPANY_WHITELIST_CREATE("AppraisalCompanyWhitelistCreateFlow", "评估机构白名单准入申请"),
    APPRAISAL_COMPANY_WHITELIST_MODIFY("AppraisalCompanyWhitelistModifyFlow", "评估机构信息变更"),
    APPRAISAL_COMPANY_WHITELIST_OUT("AppraisalCompanyWhitelistOutFlow", "评估机构白名单出库");

    private final String modelKey;
    private final String display;

    public static List<String> appraisalCompanyWhitelistModelKeys() {
        return Arrays.asList(APPRAISAL_COMPANY_WHITELIST_CREATE, APPRAISAL_COMPANY_WHITELIST_MODIFY, APPRAISAL_COMPANY_WHITELIST_OUT)
                .stream()
                .map(LeaseholdPropertyProcessModel::getModelKey)
                .collect(Collectors.toList());
    }
}
