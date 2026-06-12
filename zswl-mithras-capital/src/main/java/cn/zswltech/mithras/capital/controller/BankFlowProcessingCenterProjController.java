package cn.zswltech.mithras.capital.controller;

import cn.zswltech.mithras.api.capital.BankFlowProcessingCenterProjectApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.capital.service.BankFlowProcessingCenterProjApplicationService;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.capital.BankCenterSubTableProjectListREQ;
import cn.zswltech.mithras.dto.capital.BankCenterSubTableProjectListRSP;
import cn.zswltech.mithras.dto.capital.BankFlowConfirmIncomeREQ;
import cn.zswltech.mithras.dto.capital.BankFlowContractReceiptListRSP;
import cn.zswltech.mithras.dto.capital.BankFlowContractReceiptREQ;
import cn.zswltech.mithras.dto.capital.BankFlowNettingRefundREQ;
import cn.zswltech.mithras.dto.capital.BankFlowProcessingCenterListREQ;
import cn.zswltech.mithras.dto.capital.BankFlowProcessingCenterListRSP;
import cn.zswltech.mithras.dto.capital.BankFlowProcessingCenterManualPullFlowREQ;
import cn.zswltech.mithras.dto.capital.BankFlowProcessingCenterProjDetailRSP;
import cn.zswltech.mithras.dto.capital.BankFlowProcessingCenterProjWriteOffREQ;
import cn.zswltech.mithras.dto.capital.CashFlowCodeListREQ;
import cn.zswltech.mithras.dto.capital.FinanceFlowDeleteREQ;
import cn.zswltech.mithras.dto.capital.NoProcessingRequireREQ;
import cn.zswltech.mithras.dto.capital.ProjAmountDetailREQ;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

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
