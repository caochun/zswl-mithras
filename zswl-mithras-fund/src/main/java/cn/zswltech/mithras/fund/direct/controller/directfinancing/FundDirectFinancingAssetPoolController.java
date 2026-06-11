package cn.zswltech.mithras.fund.direct.controller.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingAssetPoolApi;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolModifyREQ;
import cn.zswltech.mithras.fund.direct.application.directfinancing.FundDirectFinancingAssetPoolApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class FundDirectFinancingAssetPoolController implements FundDirectFinancingAssetPoolApi {

    @Resource
    private FundDirectFinancingAssetPoolApplicationService fundDirectFinancingAssetPoolApplicationService;

    @Override
    public R<Void> modify(FundDirectFinancingAssetPoolModifyREQ req) {
        return fundDirectFinancingAssetPoolApplicationService.modify(req);
    }

    @Override
    public R<FundDirectFinancingAssetPoolDetailRSP> detail(FundDirectFinancingAssetPoolDetailREQ req) {
        return fundDirectFinancingAssetPoolApplicationService.detail(req);
    }
}
