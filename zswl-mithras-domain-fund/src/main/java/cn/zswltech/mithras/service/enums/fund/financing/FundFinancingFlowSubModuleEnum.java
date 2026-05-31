package cn.zswltech.mithras.service.enums.fund.financing;

import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/22
 * @description
 */
@Getter
public enum FundFinancingFlowSubModuleEnum {
    /**
     * 新增 全量数据
     */
    CREATE_ALL,

    /**
     * 编辑 全量数据
     */
    MODIFY_ALL,

    /**
     * 融资起息
     */
    CARRY_INTEREST,

    /**
     * LPR调整
     */
    LPR_CHANGE,

    /**
     * 提前结清
     */
    EARLY_SETTLE
}
