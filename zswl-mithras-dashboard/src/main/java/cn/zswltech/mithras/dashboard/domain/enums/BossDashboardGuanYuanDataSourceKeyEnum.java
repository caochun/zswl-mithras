package cn.zswltech.mithras.dashboard.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/17/10:23
 * @description 观远数据集key
 */
@Getter
@AllArgsConstructor
public enum BossDashboardGuanYuanDataSourceKeyEnum {
    AssetsOverviewByCompany("资产总览-按公司统计"),
    AssetsOverviewGroupByCityByCompany("资产总览-城市维度-按公司统计"),
    AssetsClientDistributionClientStatisticsByCompany("资产及客户分布-各阶段客户-按公司统计"),
    AssetsClientDistributionClientDeptByCompany("资产及客户分布-客户部门分布-按公司统计"),
    AssetsIndustryDistributionByCompany("资产及客户分布-资产行业分布-按公司统计"),
    BusinessTransformFunnelAllByCompany("业务转化漏斗-历史合计-按公司统计"),
    BusinessTransformFunnelThisYearByCompany("业务转化漏斗-本年合计-按公司统计"),
    AssetsFiveClassifyYearQuarter("资产五级分类-已完成季度汇总"),
    AssetsFiveClassifyStatisticsByCompany("资产五级分类-按公司统计"),
    OverdueProjectList("逾期项目列表-按公司统计"),
    OperationEfficiencyStageLX("项目运营效率-立项"),
    OperationEfficiencyStagePS("项目运营效率-评审"),
    OperationEfficiencyStagePSJSWCJHT("项目运营效率-评审结束未创建合同"),
    OperationEfficiencyStageQY("项目运营效率-签约"),
    OperationEfficiencyStageFK("项目运营效率-付款"),
    OperationEfficiencyStageTF("项目运营效率-投放"),
    OperationYYContractApproval("运营合同审批-时效"),
    OperationYYContractApprovalArrive("运营合同审批-节点到达时间-时效"),
    YunYingManageReportZLHeTongShiXiao("运营管报-合同（仅租赁类型）耗时ETL-结果"),
    BusinessContractSummaryETLResult("业务合同情况汇总表-ETL结果"),
    ProjectSituation("项目情况表"),
    PayIncomeETLResult("投放收益率ETL结果")
    ;

    private final String display;
}
