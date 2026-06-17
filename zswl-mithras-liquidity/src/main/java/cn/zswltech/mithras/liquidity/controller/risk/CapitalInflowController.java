package cn.zswltech.mithras.liquidity.controller.risk;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import java.util.List;
import cn.zswltech.mithras.api.liquidityrisk.CapitalInflowApi;
import cn.zswltech.mithras.liquidity.application.risk.CapitalInflowApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class CapitalInflowController implements CapitalInflowApi {
    @Resource
    private CapitalInflowApplicationService capitalInflowApplicationService;

    @Override
    public R<AssetInflowDetailRSP> inFlowDetail(AssetInflowDetailREQ req) {
        return capitalInflowApplicationService.inFlowDetail(req);
    }

    @Override
    public R<Void> inFlowDetailDownload(AssetInflowDetailREQ req) {
        return capitalInflowApplicationService.inFlowDetailDownload(req);
    }

    @Override
    public R<ShortTermLoanDetailRSP> shortTermLoanDetail(ShortTermLoanDetailREQ req) {
        return capitalInflowApplicationService.shortTermLoanDetail(req);
    }

    @Override
    public R<Void> shortTermLoanDownload(ShortTermLoanDetailREQ req) {
        return capitalInflowApplicationService.shortTermLoanDownload(req);
    }

    @Override
    public R<List<ChartQueryRSP>> chartQuery(ChartQueryREQ req) {
        return capitalInflowApplicationService.chartQuery(req);
    }
}
