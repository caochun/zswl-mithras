package cn.zswltech.mithras.contract.enums.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 合同 流程 二级子模块枚举
 * 标记 业务详情页要展示什么数据
 *
 * @author wangchuanhao
 * @date 2022/8/23 11:24 AM
 */
@AllArgsConstructor
@Getter
public enum ContractFlowSubModuleEnum {

    /**
     * 新增 全量数据
     */
    CREATE_ALL,

    /**
     * 编辑 全量数据
     */
    MODIFY_ALL,

    /**
     * 合同起租
     */
    START_RENT,

    /**
     * 新增借据
     */
    ADD_NEW_RECEIPT,

    /**
     * 调息
     */
    LPR_CHANGE,

    /**
     * 提前还款
     */
    EARLY_REPAYMENT,

    /**
     * 展期
     */
    EXTENSION,

    /**
     * 调整还款计划
     */
    CHANGE_REPAY_PLAN,

    /**
     * 提前结清
     */
    EARLY_SETTLE,

    /**
     * 正常结清
     */
    NORMAL_SETTLE,

    ;

}
