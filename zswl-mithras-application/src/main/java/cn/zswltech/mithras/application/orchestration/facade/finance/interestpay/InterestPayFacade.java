package cn.zswltech.mithras.application.orchestration.facade.finance.interestpay;

import cn.zswltech.mithras.api.interestpay.InterestPayApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.interestpay.*;
import cn.zswltech.mithras.finance.monthly.application.interestpay.InterestPayApplicationService;
import cn.zswltech.mithras.application.orchestration.finance.interestpay.InterestPayService;
import cn.zswltech.mithras.application.orchestration.finance.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.application.orchestration.finance.monthly.FundsDailyCostService;
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
