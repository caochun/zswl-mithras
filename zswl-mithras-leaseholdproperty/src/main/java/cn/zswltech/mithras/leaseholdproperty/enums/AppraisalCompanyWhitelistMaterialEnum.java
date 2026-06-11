package cn.zswltech.mithras.leaseholdproperty.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@AllArgsConstructor
@Getter
public enum AppraisalCompanyWhitelistMaterialEnum {
    NORMAL("普通资料"),
    EXTRA_OUT("出库补充材料");

    private final String display;
}
