package cn.zswltech.mithras.service.enums.dashboard;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/16/16:44
 * @description
 */
@Getter
@AllArgsConstructor
public enum BossDashboardBusinessKeyEnum implements PullDown {
    AssetsOverview("资产总览"),
    AssetsClientDistribution("资产及客户分布"),
    BusinessTransformFunnel("业务转化漏斗"),
    ProjectOperationEfficiency("项目运营效率"),
    AssetsFiveClassify("资产五级分类"),
    BusinessYearPayment("业务年度投放情况"),
    BusinessMonthCollection("业务月度收入表"),
    CurrentBusinessPayReceiptRate("本年租赁业务投放收益率情况表"),
    BusinessProjectStage("业务项目阶段情况"),
    DeptPerformanceSort("部门业绩排名"),
    BusinessTrans("业务转化情况"),
    OverdueProject("逾期项目信息");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
