package cn.zswltech.mithras.fund.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.FundGuaranteeAgencyApi;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.fund.application.FundGuaranteeAgencyApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class FundGuaranteeAgencyController implements FundGuaranteeAgencyApi {
    @Resource
    private FundGuaranteeAgencyApplicationService fundGuaranteeAgencyApplicationService;

    @Override
    public R<Long> addAndSync(FundGuaranteeAgencyAddREQ req) {
        return fundGuaranteeAgencyApplicationService.addAndSync(req);
    }

    @Override
    public R<Long> addHalf(FundGuaranteeAgencyAddREQ req) {
        return fundGuaranteeAgencyApplicationService.addHalf(req);
    }

    @Override
    public R<Void> modify(FundGuaranteeAgencyModifyREQ req) {
        return fundGuaranteeAgencyApplicationService.modify(req);
    }

    @Override
    public R<FundGuaranteeAgencyDetailRSP> sync(FundGuaranteeAgencySyncREQ req) {
        return fundGuaranteeAgencyApplicationService.sync(req);
    }

    @Override
    public R<PageR<FundGuaranteeAgencyListRSP>> list(FundGuaranteeAgencyListREQ req) {
        return fundGuaranteeAgencyApplicationService.list(req);
    }

    @Override
    public R<List<FundGuaranteeAgencyListRSP>> pulldown(@Valid FundGuaranteeAgencyPullDownREQ req) {
        return fundGuaranteeAgencyApplicationService.pulldown(req);
    }

    @Override
    public R<FundGuaranteeAgencyDetailRSP> detail(FundGuaranteeAgencyDetailREQ req) {
        return fundGuaranteeAgencyApplicationService.detail(req);
    }

    @Override
    public R<Void> remove(FundGuaranteeAgencyRemoveREQ req) {
        return fundGuaranteeAgencyApplicationService.remove(req);
    }

    @Override
    public R<FundGuaranteeLimitDetailRSP> limitDetail(FundGuaranteeSingletonIdREQ req) {
        return fundGuaranteeAgencyApplicationService.limitDetail(req);
    }
}
