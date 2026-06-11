package cn.zswltech.mithras.projectprocess.controller.cashflow;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.utils.CashFlowGenerationApi;
import cn.zswltech.mithras.dto.utils.CashFlowGenerationExecREQ;
import cn.zswltech.mithras.dto.utils.CashFlowGenerationExecRSP;
import cn.zswltech.mithras.dto.utils.CashFlowGenerationExportREQ;
import cn.zswltech.mithras.dto.utils.CashFlowGenerationIrrREQ;
import cn.zswltech.mithras.dto.utils.CashFlowGenerationIrrRSP;
import cn.zswltech.mithras.projectprocess.application.cashflow.CashFlowGenerationApplicationService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class CashFlowGenerationController implements CashFlowGenerationApi {

    @Resource
    private CashFlowGenerationApplicationService cashFlowGenerationApplicationService;

    @Override
    public R<List<CashFlowGenerationExecRSP>> generate(@Valid CashFlowGenerationExecREQ req) {
        return cashFlowGenerationApplicationService.generate(req);
    }

    @Override
    public R<CashFlowGenerationIrrRSP> irr(@Valid CashFlowGenerationIrrREQ req) {
        return cashFlowGenerationApplicationService.irr(req);
    }

    @Override
    public R<List<CashFlowGenerationExecRSP>> importCashFlow(MultipartFile file) {
        return cashFlowGenerationApplicationService.importCashFlow(file);
    }

    @Override
    public R<Void> exportCashFlow(@Valid CashFlowGenerationExportREQ req) {
        return cashFlowGenerationApplicationService.exportCashFlow(req);
    }
}
