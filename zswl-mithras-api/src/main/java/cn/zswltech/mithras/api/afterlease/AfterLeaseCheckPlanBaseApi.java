package cn.zswltech.mithras.api.afterlease;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.afterlease.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/9
 * @description
 */
@Api(tags = "租后管理-检查计划相关接口")
public interface AfterLeaseCheckPlanBaseApi {
    @ApiOperation("获取检查计划列表")
    @PostMapping("/afterlease/checkplan/pagelist")
    R<PageR<AfterLeaseCheckPlanListRSP>> listCheckPlanWithPage(@RequestBody AfterLeaseCheckPlanListREQ req);

    @ApiOperation("关闭检查计划")
    @PostMapping("/afterlease/checkplan/close")
    R<Void> close(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("新建检查计划")
    @PostMapping("/afterlease/checkplan/add")
    R<Long> add(@RequestBody @Valid AfterLeaseCheckPlanBaseAddREQ req);

    @ApiOperation("修改检查计划")
    @PostMapping("/afterlease/checkplan/modify")
    R<Long> modify(@RequestBody @Valid AfterLeaseCheckPlanBaseModifyREQ req);

    @ApiOperation("取消")
    @PostMapping("/afterlease/checkplan/cancel")
    R<Void> cancel(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("发布检查计划")
    @PostMapping("/afterlease/checkplan/process/publish")
    R<Void> publish(@RequestBody @Valid AfterLeaseCheckPlanPublishREQ req);

    @ApiOperation("获取检查计划详情")
    @PostMapping("/afterlease/checkplan/detail")
    R<AfterLeaseCheckPlanDetailRSP> detail(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("获取一般检查计划详情")
    @PostMapping("/afterlease/checkplan/commonly/detail")
    R<AfterLeaseCheckPlanCommonlyDetailRSP> commonlyDetail(@RequestBody @Valid AfterLeaseCheckPlanCommonlyDetailREQ req);

    @ApiOperation("获取租后检查总结报告")
    @PostMapping("/afterlease/checkplan/report/summary/list")
    R<List<AfterLeaseCheckSummaryReportRSP>> listSummaryReport(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("提交完结审批")
    @PostMapping("/afterlease/checkplan/process/finish")
    R<Void> finish(@RequestBody @Valid SinglePkREQ req);

    // >_< 方法名是有道翻译给的翻译结果。。。。。。。。
    @ApiOperation("一键催办")
    @PostMapping("/afterlease/checkplan/cuiban")
    R<Void> cuiban(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("租后检查-审批快照-前置查询")
    @PostMapping("/afterlease/checkplan/preselect")
    R<AfterLeaseAuditFlowPreRSP> afterLeaseAuditPreSelect(@RequestBody @Valid AfterLeaseAuditFlowPreREQ request);

    @ApiOperation("租后检查-获取客户相关信息")
    @PostMapping("/afterlease/checkplan/client")
    R<AfterLeaseClientPlanRSP> afterLeaseClientPlan(@RequestBody @Valid AfterLeaseAuditFlowPreREQ request);

    @ApiOperation("租后检查-资产管理策略-新版")
    @PostMapping("/afterlease/checkplan/asset/strategy")
    R<PageR<AfterLeaseAssetStrategyRSP>> afterLeaseAssetStrategys(@RequestBody @Valid AfterLeaseAssetStrategyREQ req);

    @ApiOperation("租后检查-资产管理策略修改")
    @PostMapping("/afterlease/checkplan/asset/strategy/modify")
    R<Void> afterLeaseAssetStrategyModify(@RequestBody @Valid AfterLeaseAssetStrategyModifyREQ req);
}
