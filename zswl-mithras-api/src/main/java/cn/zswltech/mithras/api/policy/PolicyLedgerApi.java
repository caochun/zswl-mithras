package cn.zswltech.mithras.api.policy;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.policy.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2023-06-15
 * 保单台账接口
 **/
@Api(tags = "保单台账接口")
public interface PolicyLedgerApi {

    /**
     *保单台账-保单详情
     **/
    @ApiOperation("保单台账-保单详情")
    @PostMapping("/policy/ledger/detail")
    R<PolicyLedgerDetailRSP> detail(@RequestBody @Valid PolicyLedgerDetailREQ req);

    @ApiOperation("保单台账-保单续保信息")
    @PostMapping("/policy/ledger/renew/insurance")
    R<List<PolicyLedgerRenewInsuranceRSP>> renewInsurance(@RequestBody @Valid PolicyLedgerRenewInsuranceREQ req);

    /**
     *保单台账-保单信息列表
     **/
    @ApiOperation("保单台账-保单信息列表")
    @PostMapping("/policy/ledger/list")
    R<PageR<PolicyLedgerListRSP>> list(@RequestBody @Valid PolicyLedgerListREQ req);

    /**
     *保单台账-列表（excel导出）
     **/
    @ApiOperation("保单台账-列表（excel导出）")
    @PostMapping("/policy/ledger/list/export")
    R<Void> exportList(@RequestBody @Valid PolicyLedgerListExportREQ req);

    /**
     *待维护保单项目列表
     **/
    @ApiOperation("待维护保单项目列表")
    @PostMapping("/maintenance/policy/proj/list")
    R<List<PolicyMaintenanceRSP>> maintenanceList(@RequestBody @Valid PolicyMaintenanceREQ req);

    /**
     *待维护保单项目导出
     **/
    @ApiOperation("待维护保单项目导出")
    @GetMapping("/maintenance/policy/proj/export")
    R<Void> maintenanceListExport(@Valid PolicyMaintenanceListExportREQ req);
    //新增

    /**
     *保单台账-合同信息
     **/
    @ApiOperation("保单台账-合同信息")
    @PostMapping("/policy/ledger/contract/detail")
    R<PolicyLedgerContractDetailRSP> contractDetail(@RequestBody @Valid PolicyLedgerDetailREQ req);

    /**
     *保单台账-合同保单信息
     **/
    @ApiOperation("保单台账-合同保单信息")
    @PostMapping("/policy/ledger/contract/policy")
    R<List<PolicyInfoDetailRSP>> contractPolicy(@RequestBody @Valid PolicyLedgerContractPolicyREQ req);


    @ApiOperation("保单台账-合同保单信息导出")
    @PostMapping("/policy/ledger/contract/policy/export")
    R<Void> contractPolicyExport(@RequestBody @Valid PolicyLedgerContractPolicyREQ req);

    @ApiOperation("保单台账-确认保单信息")
    @PostMapping("/policy/ledger/tmp/sync")
    R<Void> sync(@RequestBody @Valid PolicyLedgerTmpSyncREQ req);

}
