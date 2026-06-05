package cn.zswltech.mithras.policy.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceTmpImportREQ;
import cn.zswltech.mithras.api.policy.PolicyInfoTmpApi;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpAddREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpListREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpListRSP;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpModifyREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoTmpRemoveREQ;
import cn.zswltech.mithras.dto.policy.PolicyTmpExportREQ;
import cn.zswltech.mithras.policy.application.PolicyInfoTmpApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class PolicyInfoTmpController implements PolicyInfoTmpApi {

    @Resource
    private PolicyInfoTmpApplicationService policyInfoTmpApplicationService;

    @Override
    public R<Long> add(@Valid PolicyInfoTmpAddREQ req) {
        return policyInfoTmpApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@Valid PolicyInfoTmpModifyREQ req) {
        return policyInfoTmpApplicationService.modify(req);
    }

    @Override
    public R<PageR<PolicyInfoTmpListRSP>> list(@Valid PolicyInfoTmpListREQ req) {
        return policyInfoTmpApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@Valid PolicyInfoTmpRemoveREQ req) {
        return policyInfoTmpApplicationService.remove(req);
    }

    @Override
    public R<String> importExcel(@Valid PaymentPoliceTmpImportREQ paymentPoliceImportREQ) {
        return policyInfoTmpApplicationService.importExcel(paymentPoliceImportREQ);
    }

    @Override
    public R<Void> policyTmpExport(@Valid PolicyTmpExportREQ req) {
        return policyInfoTmpApplicationService.policyTmpExport(req);
    }
}
