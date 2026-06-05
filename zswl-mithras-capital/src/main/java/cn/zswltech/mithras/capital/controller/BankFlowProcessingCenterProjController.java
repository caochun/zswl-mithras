package cn.zswltech.mithras.capital.controller;

import cn.zswltech.mithras.api.capital.BankFlowProcessingCenterProjectApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.capital.*;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.capital.application.BankFlowProcessingCenterProjApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class BankFlowProcessingCenterProjController implements BankFlowProcessingCenterProjectApi {
    @Resource
    private BankFlowProcessingCenterProjApplicationService bankFlowProcessingCenterProjApplicationService;

    @Override
    public R<PageR<BankFlowProcessingCenterListRSP>> selectBankCenterByTab(BankFlowProcessingCenterListREQ req) {
        return bankFlowProcessingCenterProjApplicationService.selectBankCenterByTab(req);
    }

    @Override
    public R<List<String>> projectCashFlowCodeList(CashFlowCodeListREQ req) {
        return bankFlowProcessingCenterProjApplicationService.projectCashFlowCodeList(req);
    }

    @Override
    public R<BankFlowProcessingCenterProjDetailRSP> projAmountDetail(ProjAmountDetailREQ req) {
        return bankFlowProcessingCenterProjApplicationService.projAmountDetail(req);
    }

    @Override
    public R<Void> batchWriteOff(BankFlowProcessingCenterProjWriteOffREQ req) {
        return bankFlowProcessingCenterProjApplicationService.batchWriteOff(req);
    }

    @Override
    public R<List<String>> manualPullFlow(BankFlowProcessingCenterManualPullFlowREQ req) {
        return bankFlowProcessingCenterProjApplicationService.manualPullFlow(req);
    }

    @Override
    public R<Void> noProcessingRequire(NoProcessingRequireREQ req) {
        return bankFlowProcessingCenterProjApplicationService.noProcessingRequire(req);
    }

    @Override
    public R<Void> delete(FinanceFlowDeleteREQ req) {
        return bankFlowProcessingCenterProjApplicationService.delete(req);
    }

    @Override
    public R<List<BankCenterSubTableProjectListRSP>> subListByCashFlowId(BankCenterSubTableProjectListREQ req) {
        return bankFlowProcessingCenterProjApplicationService.subListByCashFlowId(req);
    }

    @Override
    public R<Void> nettingRefund(BankFlowNettingRefundREQ req) {
        return bankFlowProcessingCenterProjApplicationService.nettingRefund(req);
    }

    @Override
    public R<Void> confirmIncome(BankFlowConfirmIncomeREQ req) {
        return bankFlowProcessingCenterProjApplicationService.confirmIncome(req);
    }

    @Override
    public R<List<BankFlowContractReceiptListRSP>> getReceiptCode(BankFlowContractReceiptREQ req) {
        return bankFlowProcessingCenterProjApplicationService.getReceiptCode(req);
    }

    @Override
    public R<Void> restoreBankFlow(@Valid MultiplePkREQ req) {
        return bankFlowProcessingCenterProjApplicationService.restoreBankFlow(req);
    }

}
