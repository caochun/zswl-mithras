package cn.zswltech.mithras.policy.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.policy.PolicyLedgerApi;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.dto.policy.PolicyLedgerContractDetailRSP;
import cn.zswltech.mithras.dto.policy.PolicyLedgerContractPolicyREQ;
import cn.zswltech.mithras.dto.policy.PolicyLedgerDetailREQ;
import cn.zswltech.mithras.dto.policy.PolicyLedgerDetailRSP;
import cn.zswltech.mithras.dto.policy.PolicyLedgerListExportREQ;
import cn.zswltech.mithras.dto.policy.PolicyLedgerListREQ;
import cn.zswltech.mithras.dto.policy.PolicyLedgerListRSP;
import cn.zswltech.mithras.dto.policy.PolicyLedgerRenewInsuranceREQ;
import cn.zswltech.mithras.dto.policy.PolicyLedgerRenewInsuranceRSP;
import cn.zswltech.mithras.dto.policy.PolicyLedgerTmpSyncREQ;
import cn.zswltech.mithras.dto.policy.PolicyMaintenanceListExportREQ;
import cn.zswltech.mithras.dto.policy.PolicyMaintenanceREQ;
import cn.zswltech.mithras.dto.policy.PolicyMaintenanceRSP;
import cn.zswltech.mithras.policy.application.PolicyLedgerApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class PolicyLedgerController implements PolicyLedgerApi {

    @Resource
    private PolicyLedgerApplicationService policyLedgerApplicationService;

    @Override
    public R<PolicyLedgerDetailRSP> detail(@Valid PolicyLedgerDetailREQ req) {
        return policyLedgerApplicationService.detail(req);
    }

    @Override
    public R<List<PolicyLedgerRenewInsuranceRSP>> renewInsurance(@Valid PolicyLedgerRenewInsuranceREQ req) {
        return policyLedgerApplicationService.renewInsurance(req);
    }

    @Override
    public R<PageR<PolicyLedgerListRSP>> list(@Valid PolicyLedgerListREQ req) {
        return policyLedgerApplicationService.list(req);
    }

    @Override
    public R<Void> exportList(@Valid PolicyLedgerListExportREQ req) {
        return policyLedgerApplicationService.exportList(req);
    }

    @Override
    public R<List<PolicyMaintenanceRSP>> maintenanceList(@Valid PolicyMaintenanceREQ req) {
        return policyLedgerApplicationService.maintenanceList(req);
    }

    @Override
    public R<Void> maintenanceListExport(@Valid PolicyMaintenanceListExportREQ req) {
        return policyLedgerApplicationService.maintenanceListExport(req);
    }

    @Override
    public R<PolicyLedgerContractDetailRSP> contractDetail(@Valid PolicyLedgerDetailREQ req) {
        return policyLedgerApplicationService.contractDetail(req);
    }

    @Override
    public R<List<PolicyInfoDetailRSP>> contractPolicy(@Valid PolicyLedgerContractPolicyREQ req) {
        return policyLedgerApplicationService.contractPolicy(req);
    }

    @Override
    public R<Void> contractPolicyExport(@Valid PolicyLedgerContractPolicyREQ req) {
        return policyLedgerApplicationService.contractPolicyExport(req);
    }

    @Override
    public R<Void> sync(@Valid PolicyLedgerTmpSyncREQ req) {
        return policyLedgerApplicationService.sync(req);
    }
}
