package cn.zswltech.mithras.fund.interfaces.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingRepayEstimateApi;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualImportRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayEstimateListRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayImportREQ;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingRepayEstimateApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingRepayEstimateController implements FundFinancingRepayEstimateApi {
    @Resource
    private FundFinancingRepayEstimateApplicationService fundFinancingRepayEstimateApplicationService;

    @Override
    public R<List<FundFinancingRepayEstimateListRSP>> list(@Valid SingleFinancingIdREQ req) {
        return fundFinancingRepayEstimateApplicationService.list(req);
    }

    @Override
    public R<FundFinancingRepayActualImportRSP> importExcel(@Valid FundFinancingRepayImportREQ req) {
        return fundFinancingRepayEstimateApplicationService.importExcel(req);
    }

    @Override
    public void exportExcel(@Valid SingleFinancingIdREQ req) {
        fundFinancingRepayEstimateApplicationService.exportExcel(req);
    }
}
