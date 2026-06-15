package cn.zswltech.mithras.application.orchestration.facade.policy;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceImportREQ;
import cn.zswltech.mithras.policy.application.info.PolicyInfoApplicationService;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.policy.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.policy.PolicyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2023-06-15
 **/

@Slf4j
@Service
public class PolicyInfoFacade implements PolicyInfoApplicationService {

    @Resource
    private PolicyInfoService policyInfoService;

    @Override
    public R<List<SelectRSP>> projList() {
        return R.ok(policyInfoService.projList());
    }

    @Override
    public R<List<SelectRSP>> contractList(PolicyAddContractREQ req) {
        return R.ok(policyInfoService.contractList(req));
    }

    @Override
    public R<Long> add(@Valid PolicyInfoAddREQ req) {
        return R.ok(policyInfoService.add(req));
    }

    @Override
    public R<Void> submit(PolicyInfoSubmitREQ req) {
        policyInfoService.submit(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(@Valid PolicyInfoModifyREQ req) {
        policyInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PolicyInfoDetailRSP> detail(@Valid PolicyInfoDetailREQ req) {
        return R.ok(policyInfoService.detail(req));
    }

    @Override
    public R<PageR<PolicyInfoListRSP>> list(@Valid PolicyInfoListREQ req) {
        return R.ok(policyInfoService.list(req));
    }

    @Override
    public R<Void> remove(@Valid PolicyInfoRemoveREQ req) {
        policyInfoService.remove(req);
        return R.ok();
    }

    @Override
    public R<String> importExcel(@Valid PaymentPoliceImportREQ paymentPoliceImportREQ) {
        return R.ok(policyInfoService.importExcel(paymentPoliceImportREQ));
    }
}
