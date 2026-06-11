package cn.zswltech.mithras.finance.controller.profitcalculate;

import cn.zswltech.mithras.api.ProfitCalculateResultApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ProfitCalculateResultListREQ;
import cn.zswltech.mithras.dto.ProfitCalculateResultListRSP;
import cn.zswltech.mithras.finance.application.profitcalculate.api.ProfitCalculateResultApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class ProfitCalculateResultController implements ProfitCalculateResultApi {

    @Resource
    private ProfitCalculateResultApplicationService profitCalculateResultApplicationService;

    @Override
    public R<ProfitCalculateResultListRSP> pageList(@Valid ProfitCalculateResultListREQ req) {
        return profitCalculateResultApplicationService.pageList(req);
    }

    @Override
    public void exportExcel(@Valid ProfitCalculateResultListREQ req) {
        profitCalculateResultApplicationService.exportExcel(req);
    }
}
