package cn.zswltech.mithras.policy.controller.info;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceImportREQ;
import cn.zswltech.mithras.api.policy.PolicyInfoApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.policy.PolicyAddContractREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoAddREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.dto.policy.PolicyInfoListREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoListRSP;
import cn.zswltech.mithras.dto.policy.PolicyInfoModifyREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoRemoveREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoSubmitREQ;
import cn.zswltech.mithras.policy.application.info.PolicyInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class PolicyInfoController implements PolicyInfoApi {

    @Resource
    private PolicyInfoApplicationService policyInfoApplicationService;

    @Override
    public R<List<SelectRSP>> projList() {
        return policyInfoApplicationService.projList();
    }

    @Override
    public R<List<SelectRSP>> contractList(PolicyAddContractREQ req) {
        return policyInfoApplicationService.contractList(req);
    }

    @Override
    public R<Long> add(@Valid PolicyInfoAddREQ req) {
        return policyInfoApplicationService.add(req);
    }

    @Override
    public R<Void> submit(@Valid PolicyInfoSubmitREQ req) {
        return policyInfoApplicationService.submit(req);
    }

    @Override
    public R<Void> modify(@Valid PolicyInfoModifyREQ req) {
        return policyInfoApplicationService.modify(req);
    }

    @Override
    public R<PolicyInfoDetailRSP> detail(@Valid PolicyInfoDetailREQ req) {
        return policyInfoApplicationService.detail(req);
    }

    @Override
    public R<PageR<PolicyInfoListRSP>> list(@Valid PolicyInfoListREQ req) {
        return policyInfoApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@Valid PolicyInfoRemoveREQ req) {
        return policyInfoApplicationService.remove(req);
    }

    @Override
    public R<String> importExcel(@Valid PaymentPoliceImportREQ paymentPoliceImportREQ) {
        return policyInfoApplicationService.importExcel(paymentPoliceImportREQ);
    }
}
