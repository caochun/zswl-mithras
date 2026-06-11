package cn.zswltech.mithras.liquidity.controller.liquidityrisk;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import java.util.List;
import cn.zswltech.mithras.api.liquidityrisk.CapitalOutflowAPI;
import cn.zswltech.mithras.liquidity.application.liquidityrisk.CapitalOutflowApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class CapitalOutflowController implements CapitalOutflowAPI {
    @Resource
    private CapitalOutflowApplicationService capitalOutflowApplicationService;

    @Override
    public R<FundsCashOutflowListRsp> fundsCashOutflowList(CashOutflowListReq req) {
        return capitalOutflowApplicationService.fundsCashOutflowList(req);
    }

    @Override
    public R<AssetsCashOutflowListRsp> assetsCashOutflowList(CashOutflowListReq req) {
        return capitalOutflowApplicationService.assetsCashOutflowList(req);
    }

    @Override
    public R<ChartQueryRSP> estimateCashOutflowList(CashOutflowListReq req) {
        return capitalOutflowApplicationService.estimateCashOutflowList(req);
    }

    @Override
    public R<ChartQueryRSP> stressTestingOutflowList(CashOutflowListReq req) {
        return capitalOutflowApplicationService.stressTestingOutflowList(req);
    }

    @Override
    public R<Void> cashOutflowExport(CashOutflowListReq req) {
        return capitalOutflowApplicationService.cashOutflowExport(req);
    }

    @Override
    public R<List<CashInOutStatRSP>> cashInOutStat(CashInOutStatREQ req) {
        return capitalOutflowApplicationService.cashInOutStat(req);
    }

    @Override
    public void downloadInOutStat(CashInOutStatREQ req) {
        capitalOutflowApplicationService.downloadInOutStat(req);
    }
}
