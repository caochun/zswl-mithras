package cn.zswltech.mithras.api.afterlease;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
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
 * @date 2022/11/10
 * @description
 */
@Api(tags = "租后管理-检查计划关联项目（更改为客户）相关接口")
public interface AfterLeaseCheckClientApi {
    @ApiOperation("获取检查计划项目（更改为客户）信息")
    @PostMapping("/afterlease/checkplan/project/info/get")
    R<AfterLeaseCheckClientInfoRSP> getInfoById(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("非季度检查计划查询项目（更改为客户）")
    @PostMapping("/afterlease/checkplan/project/query")
    R<List<AfterLeaseCheckClientSelectRSP>> queryClient(@RequestBody @Valid AfterLeaseCheckClientSelectREQ req);

    @ApiOperation("项目（更改为客户）检查提交审批")
    @PostMapping("/afterlease/checkplan/project/process/submit")
    R<Void> submitApproval(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("变更项目（更改为客户）报告类型（变更模板）")
    @PostMapping("/afterlease/checkplan/project/reporttype/change")
    R<AfterLeaseCheckReportVersionRSP> changeReportType(@RequestBody @Valid AfterLeaseCheckReportTypeChangeREQ req);

    @Deprecated
    @ApiOperation("获取按照业务部门分组的项目（更改为客户）信息")
    @PostMapping("/afterlease/checkplan/project/listGroupByDept")
    R<List<AfterLeaseCheckClientDeptInfoRSP>> listAfterLeaseCanCheckGroupByDept(@RequestBody @Valid SinglePkREQ req);

    @Deprecated
    @ApiOperation("获取检查项目（更改为客户）列表")
    @PostMapping("/afterlease/checkplan/project/list")
    R<List<AfterLeaseCheckClientListRSP>> list(@RequestBody @Valid AfterLeaseCheckClientListREQ req);

    @ApiOperation("保存需检查项目（更改为客户）")
    @PostMapping("/afterlease/checkplan/project/save")
    R<Void> save(@RequestBody @Valid AfterLeaseCheckClientSaveREQ req);

    @ApiOperation("移除被选择的检查项目（更改为客户）")
    @PostMapping("/afterlease/checkplan/project/remove")
    R<Void> remove(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("非季度计划获取计划项目（更改为客户）列表")
    @PostMapping("/afterlease/checkplan/notquarter/project/list")
    R<List<AfterLeaseCheckClientListRSP>> listNotQuarterPlanProject(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("季度计划获取计划项目（更改为客户）列表")
    @PostMapping("/afterlease/checkplan/quarter/project/list")
    R<List<AfterLeaseCheckClientListGroupRSP>> listQuarterPlanProject(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("检查计划客户变更")
    @PostMapping("/afterlease/checkplan/quarter/project/modify")
    R<Void> modifyPlanClient(@RequestBody @Valid AfterLeaseCheckClientModifyReq req);

    @ApiOperation("获取协查风控经理列表")
    @PostMapping("/afterlease/checkplan/riskmanager/list")
    R<List<SelectRSP>> listRiskManager();

    @ApiOperation("获取计划台账列表")
    @PostMapping("/afterlease/checkplan/ledger/list")
    R<PageR<AfterLeaseCheckLedgerListRSP>> queryCheckPlanLedgerList(@RequestBody @Valid AfterLeaseCheckLedgerListREQ req);

}
