package cn.zswltech.mithras.fund.directfinancing.controller.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingRepayActualApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.fund.directfinancing.application.directfinancing.FundDirectFinancingRepayActualApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FundDirectFinancingRepayActualController implements FundDirectFinancingRepayActualApi {

    @Resource
    private FundDirectFinancingRepayActualApplicationService fundDirectFinancingRepayActualApplicationService;

    @Override
    public R<FundDirectFinancingRepayActualImportRSP> importExcel(FundDirectFinancingRepayActualImportREQ req) {
        return fundDirectFinancingRepayActualApplicationService.importExcel(req);
    }

    @Override
    public void exportExcel(FundDirectFinancingRepayActualExportREQ req) {
        fundDirectFinancingRepayActualApplicationService.exportExcel(req);
    }

    @Override
    public R<PageR<FundDirectFinancingRepayActualListRSP>> list(FundDirectFinancingRepayActualListREQ req) {
        return fundDirectFinancingRepayActualApplicationService.list(req);
    }

    @Override
    public R<List<FundDirectRepayActualSplitRSP>> splitList(SingleFinancingIdREQ req) {
        return fundDirectFinancingRepayActualApplicationService.splitList(req);
    }

    @Override
    public R<Void> calculate(SingleFinancingIdREQ req) {
        return fundDirectFinancingRepayActualApplicationService.calculate(req);
    }
}
