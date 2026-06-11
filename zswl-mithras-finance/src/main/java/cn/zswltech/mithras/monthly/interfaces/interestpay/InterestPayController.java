package cn.zswltech.mithras.monthly.interfaces.interestpay;

import cn.zswltech.mithras.api.interestpay.InterestPayApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.interestpay.*;
import cn.zswltech.mithras.monthly.application.interestpay.InterestPayApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class InterestPayController implements InterestPayApi {

    @Resource
    private InterestPayApplicationService interestPayApplicationService;

    @Override
    public R<List<InterestPayRSP>> listPage(InterestPayListREQ req) {
        return interestPayApplicationService.listPage(req);
    }

    @Override
    public R<List<InterestPayRSP>> calculate(InterestPayREQ req) {
        return interestPayApplicationService.calculate(req);
    }

    @Override
    public R<InterestPayBasicDetailRSP> basicDetail(InterestPayBasicDetailREQ req) {
        return interestPayApplicationService.basicDetail(req);
    }

    @Override
    public R<List<InterestPayCalDetailMultiRSP>> calDetail(InterestPayCalDetailREQ req) {
        return interestPayApplicationService.calDetail(req);
    }

    @Override
    public R<Void> modify(InterestPayCalDetailModifyREQ req) {
        return interestPayApplicationService.modify(req);
    }
}
