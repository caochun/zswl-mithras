package cn.zswltech.mithras.funddirect.interfaces.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingFeeDetailApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.funddirect.application.directfinancing.FundDirectFinancingFeeDetailApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class FundDirectFinancingFeeDetailController implements FundDirectFinancingFeeDetailApi {

    @Resource
    private FundDirectFinancingFeeDetailApplicationService fundDirectFinancingFeeDetailApplicationService;

    @Override
    public R<Void> modifyProgramme(FundDirectFinancingProgrammeModifyREQ req) {
        return fundDirectFinancingFeeDetailApplicationService.modifyProgramme(req);
    }

    @Override
    public R<Void> add(FundDirectFinancingFeeDetailAddREQ req) {
        return fundDirectFinancingFeeDetailApplicationService.add(req);
    }

    @Override
    public R<Void> modifyFee(FundDirectFinancingFeeDetailModifyREQ req) {
        return fundDirectFinancingFeeDetailApplicationService.modifyFee(req);
    }

    @Override
    public R<FundDirectFinancingFeeDetailRSP> detail(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingFeeDetailApplicationService.detail(req);
    }

    @Override
    public R<FundDirectFinancingFeeDetailListRSP> list(FundDirectFinancingFeeDetailListREQ req) {
        return fundDirectFinancingFeeDetailApplicationService.list(req);
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingFeeDetailApplicationService.remove(req);
    }
}
