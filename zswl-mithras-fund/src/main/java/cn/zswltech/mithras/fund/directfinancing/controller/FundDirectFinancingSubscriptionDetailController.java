package cn.zswltech.mithras.fund.directfinancing.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingSubscriptionDetailApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.fund.directfinancing.application.FundDirectFinancingSubscriptionDetailApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class FundDirectFinancingSubscriptionDetailController implements FundDirectFinancingSubscriptionDetailApi {

    @Resource
    private FundDirectFinancingSubscriptionDetailApplicationService fundDirectFinancingSubscriptionDetailApplicationService;

    @Override
    public R<Void> add(FundDirectFinancingSubscriptionDetailAddREQ req) {
        return fundDirectFinancingSubscriptionDetailApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundDirectFinancingSubscriptionDetailModifyREQ req) {
        return fundDirectFinancingSubscriptionDetailApplicationService.modify(req);
    }

    @Override
    public R<FundDirectFinancingSubscriptionDetailListRSP> list(FundDirectFinancingSubscriptionDetailListREQ req) {
        return fundDirectFinancingSubscriptionDetailApplicationService.list(req);
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingSubscriptionDetailApplicationService.remove(req);
    }

    @Override
    public void exportExcel(FundDirectFinancingProductDetailListREQ req) {
        fundDirectFinancingSubscriptionDetailApplicationService.exportExcel(req);
    }
}
