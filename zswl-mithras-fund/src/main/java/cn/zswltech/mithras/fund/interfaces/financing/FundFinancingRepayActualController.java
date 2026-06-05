package cn.zswltech.mithras.fund.interfaces.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingRepayActualApi;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualImportRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualListRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayImportREQ;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingRepayActualApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingRepayActualController implements FundFinancingRepayActualApi {
    @Resource
    private FundFinancingRepayActualApplicationService fundFinancingRepayActualApplicationService;

    @Override
    public R<List<FundFinancingRepayActualListRSP>> list(@Valid SingleFinancingIdREQ req) {
        return fundFinancingRepayActualApplicationService.list(req);
    }

    @Override
    public R<FundFinancingRepayActualImportRSP> importExcel(@Valid FundFinancingRepayImportREQ req) {
        return fundFinancingRepayActualApplicationService.importExcel(req);
    }

    @Override
    public void exportExcel(@Valid SingleFinancingIdREQ req) {
        fundFinancingRepayActualApplicationService.exportExcel(req);
    }
}
