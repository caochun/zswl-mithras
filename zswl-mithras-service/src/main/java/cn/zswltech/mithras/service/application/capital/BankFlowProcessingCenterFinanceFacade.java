package cn.zswltech.mithras.service.application.capital;

import cn.zswltech.mithras.capital.application.BankFlowProcessingCenterFinanceApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.*;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualSplitRecordService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualSplitService;
import cn.zswltech.mithras.service.service.capital.BankFlowProcessingCenterFinanceService;
import cn.zswltech.mithras.service.service.capital.FinanceFlowAutoWriteOffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/19/18:43
 * @description
 */
@Slf4j
@Service
public class BankFlowProcessingCenterFinanceFacade implements BankFlowProcessingCenterFinanceApplicationService {
    @Resource
    private BankFlowProcessingCenterFinanceService bankFlowProcessingCenterFinanceService;
    @Resource
    private FinanceFlowAutoWriteOffService financeFlowAutoWriteOffService;
    @Resource
    private FundDirectFinancingRepayActualSplitService fundDirectFinancingRepayActualSplitService;
    @Resource
    private FundDirectFinancingRepayActualSplitRecordService fundDirectFinancingRepayActualSplitRecordService;

    @Override
    public R<List<BankFlowProcessingCenterFinanceOrgRSP>> listOrg(BankFlowProcessingCenterFinanceOrgREQ req) {
        return R.ok(bankFlowProcessingCenterFinanceService.
                listOrg(req));
    }

    @Override
    public R<List<BankFlowProcessingCenterFinanceInfoRSP>> listFinanceInfo(BankFlowProcessingCenterFinanceInfoREQ req) {
        return R.ok(bankFlowProcessingCenterFinanceService.listFinanceInfo(req));
    }

    @Override
    public R<List<BankFlowProcessingCenterFinanceCashFlowRSP>> listCashFlow(BankFlowProcessingCenterFinanceCashFlowREQ req) {
        return R.ok(bankFlowProcessingCenterFinanceService.listCashFlow(req));
    }

    @Override
    public R<BankFlowProcessingCenterFinancePaymentCashFlowSumRSP> listPaymentCashFlow(BankFlowProcessingCenterFinancePaymentCashFlowREQ req) {
        return R.ok(bankFlowProcessingCenterFinanceService.listPaymentCashFlow(req));
    }

    @Override
    public R<List<BankCenterSubTableFinanceListRSP>> subList(@Valid BankCenterSubTableFinanceListREQ req) {
        return R.ok(bankFlowProcessingCenterFinanceService.subList(req));
    }

    @Override
    public R<Void> financingPaymentWriteOff(FinancePaymentWriteOffREQ req) {
        log.info("资金端付款核销开始， req={}", req);
        // 校验
        financeFlowAutoWriteOffService.checkFinancingPaymentWriteOff(req);
        financeFlowAutoWriteOffService.financingPaymentWriteOff(req);
        return R.ok();
    }

    @Override
    public R<List<BankFlowProcessingCenterListRSP>> listFlow(BankFlowQueryREQ req) {
        return R.ok(bankFlowProcessingCenterFinanceService.listFlow(req));
    }

    @Override
    public R<List<FinanceRepaySplitRSP>> listRepaySplit(FinanceRepaySplitREQ req) {
        return R.ok(fundDirectFinancingRepayActualSplitService.listRepaySplitRsp(req));
    }

    @Override
    public R<List<FinanceRepaySplitRecordRSP>> listRepaySplitWriteOffRecord(FinanceRepaySplitRecordREQ req) {
        return R.ok(fundDirectFinancingRepayActualSplitRecordService.listRepaySplitWriteOffRecord(req));
    }
}
