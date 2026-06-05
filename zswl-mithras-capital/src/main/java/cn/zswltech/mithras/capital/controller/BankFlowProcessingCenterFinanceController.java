package cn.zswltech.mithras.capital.controller;

import cn.zswltech.mithras.api.capital.BankFlowProcessingCenterFinanceApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.*;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.capital.application.BankFlowProcessingCenterFinanceApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class BankFlowProcessingCenterFinanceController implements BankFlowProcessingCenterFinanceApi {
    @Resource
    private BankFlowProcessingCenterFinanceApplicationService bankFlowProcessingCenterFinanceApplicationService;

    @Override
    public R<List<BankFlowProcessingCenterFinanceOrgRSP>> listOrg(BankFlowProcessingCenterFinanceOrgREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.listOrg(req);
    }

    @Override
    public R<List<BankFlowProcessingCenterFinanceInfoRSP>> listFinanceInfo(BankFlowProcessingCenterFinanceInfoREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.listFinanceInfo(req);
    }

    @Override
    public R<List<BankFlowProcessingCenterFinanceCashFlowRSP>> listCashFlow(BankFlowProcessingCenterFinanceCashFlowREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.listCashFlow(req);
    }

    @Override
    public R<BankFlowProcessingCenterFinancePaymentCashFlowSumRSP> listPaymentCashFlow(BankFlowProcessingCenterFinancePaymentCashFlowREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.listPaymentCashFlow(req);
    }

    @Override
    public R<List<BankCenterSubTableFinanceListRSP>> subList(@Valid BankCenterSubTableFinanceListREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.subList(req);
    }

    @Override
    public R<Void> financingPaymentWriteOff(FinancePaymentWriteOffREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.financingPaymentWriteOff(req);
    }

    @Override
    public R<List<BankFlowProcessingCenterListRSP>> listFlow(BankFlowQueryREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.listFlow(req);
    }

    @Override
    public R<List<FinanceRepaySplitRSP>> listRepaySplit(FinanceRepaySplitREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.listRepaySplit(req);
    }

    @Override
    public R<List<FinanceRepaySplitRecordRSP>> listRepaySplitWriteOffRecord(FinanceRepaySplitRecordREQ req) {
        return bankFlowProcessingCenterFinanceApplicationService.listRepaySplitWriteOffRecord(req);
    }

}
