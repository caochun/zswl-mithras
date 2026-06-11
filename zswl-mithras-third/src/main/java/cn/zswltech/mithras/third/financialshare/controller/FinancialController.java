package cn.zswltech.mithras.third.financialshare.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.third.FinancialApi;
import cn.zswltech.mithras.dto.third.financial.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import cn.zswltech.mithras.third.financialshare.application.FinancialApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinancialController implements FinancialApi {
    @Resource
    private FinancialApplicationService financialApplicationService;

    @Override
    public R<String> paymentRecode(@Valid ThirdPaymentDetailREQ req) {
        return financialApplicationService.paymentRecode(req);
    }

    @Override
    public R<String> collectionRecode(@Valid ThirdCollectionRecordREQ req) {
        return financialApplicationService.collectionRecode(req);
    }

    @Override
    public R<String> addBackRecord(@Valid ThirdMarginRecordREQ req) {
        return financialApplicationService.addBackRecord(req);
    }

    @Override
    public R<Void> innerRecord(InnerCollectionRecordREQ req) {
        return financialApplicationService.innerRecord(req);
    }

    @Override
    public R<List<ThirdFinancialWithdrawRSP>> withdraw(List<ThirdFinancialWithdrawREQ> reqs) {
        return financialApplicationService.withdraw(reqs);
    }
}
