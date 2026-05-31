package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.service.enums.projreview.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程变量
 *
 * @author wangchuanhao
 * @date 2022/9/8 4:17 PM
 */
@AllArgsConstructor
@Getter
public enum ProcessVarEnum {
    /**
     * 合同变更-提前还款财务确认节点是否通过
     */
    contractEarlyRepayFinancialConfirmPass(Boolean.class),

    /**
     * 合同变更-提前还款提交流程时的初始提前还款日期 yyyy-MM-dd
     */
    contractEarlyRepayOriginalDate(String.class),

    /**
     * 合同创建-合同文本类型是否过法务节点
     */
    contractCreateSkipLawFlag(Boolean.class),

    /**
     * 合同其他变更-合同文本类型是否过法务节点
     */
    contractModifySkipLawFlag(Boolean.class),

    /**
     * 客户风控行业分类
     */
    clientRiskControlIndustryClassify(String.class),

    /**
     * 风控行业分类是否是公用事业或民生消费
     */
    isPublicOrCivil(Boolean.class),

    /**
     * 风控行业分类是否是产业类
     */
    isIndustry(Boolean.class),

    /**
     * 是否在省内
     * @deprecated 董事会节点进入条件修改
     */
    @Deprecated
    InsideProvinceFlag(Boolean.class),

    /**
     * 项目类型
     * @deprecated 董事会节点进入条件修改
     */
    @Deprecated
    ProjectType(String.class),

    /**
     * 风险敞口（元）
     */
    riskExposure(Double.class),

    /**
     * 项目评审/授信评审，会议纪要评审汇票节点，秘书选择是否要过董事会
     */
    projReviewChooseBorad(Boolean.class),

    /**
     * 项目评审是否需要董事会审批
     * 合同、付款模块需要此变量判断是否走向董事长
     */
    projReviewNeedBoradApprove(Boolean.class),

    /**
     * 项目评审是否需要评审会主任审批
     */
    projReviewHasJuryDirector(Boolean.class),

    /**
     * 合同金额（元）
     */
    applyCreditAmount(Double.class),
    ;

    private Class type;

}
