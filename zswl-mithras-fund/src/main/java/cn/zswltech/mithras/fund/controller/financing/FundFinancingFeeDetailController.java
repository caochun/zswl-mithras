package cn.zswltech.mithras.fund.controller.financing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingFeeDetailApi;
import cn.zswltech.mithras.dto.fund.financing.fee.*;
import javax.annotation.Resource;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingFeeDetailApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingFeeDetailController implements FundFinancingFeeDetailApi {
    @Resource
    private FundFinancingFeeDetailApplicationService fundFinancingFeeDetailApplicationService;

    @Override
    public R<Void> add(FundFinancingFeeDetailAddREQ req) {
        return fundFinancingFeeDetailApplicationService.add(req);
    }

    @Override
    public R<Void> modifyFee(FundFinancingFeeDetailModifyREQ req) {
        return fundFinancingFeeDetailApplicationService.modifyFee(req);
    }

    @Override
    public R<PageR<FundFinancingFeeDetailRSP>> list(FundFinancingFeeDetailListREQ req) {
        return fundFinancingFeeDetailApplicationService.list(req);
    }

    @Override
    public R<Void> remove(FundFinancingFeeSingleIdREQ req) {
        return fundFinancingFeeDetailApplicationService.remove(req);
    }
}
