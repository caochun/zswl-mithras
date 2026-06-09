package cn.zswltech.mithras.application.facade.finance.interestpay;

import cn.zswltech.mithras.api.InterestPay.InterestPayApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.interestPay.*;
import cn.zswltech.mithras.monthly.application.interestpay.InterestPayApplicationService;
import cn.zswltech.mithras.service.service.interestPay.InterestPayService;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class InterestPayFacade implements InterestPayApplicationService {

    @Resource
    private InterestPayService interestPayService;
    @Resource
    private FundsDailyCostMainService fundsDailyCostMainService;
    @Resource
    private FundsDailyCostService fundsDailyCostService;

    @Override
    public R<List<InterestPayRSP>> listPage(InterestPayListREQ req) {
//        return R.ok(interestPayService.listPage(req));
        return R.ok(fundsDailyCostMainService.listInterestPayRSP(req));
    }

    @Override
    public R<List<InterestPayRSP>> calculate(InterestPayREQ req) {
        return R.ok(interestPayService.calculate(req));
    }

    @Override
    public R<InterestPayBasicDetailRSP> basicDetail(InterestPayBasicDetailREQ req) {
        return R.ok(interestPayService.basicDetail(req));
    }

    @Override
    public R<List<InterestPayCalDetailMultiRSP>> calDetail(InterestPayCalDetailREQ req) {
        return R.ok(interestPayService.calDetail2(req));
    }

    @Override
    public R<Void> modify(InterestPayCalDetailModifyREQ req) {
        fundsDailyCostService.modify(req);
        return R.ok();
    }
}
