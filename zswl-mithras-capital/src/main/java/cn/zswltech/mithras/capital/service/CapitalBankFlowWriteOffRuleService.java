package cn.zswltech.mithras.capital.service;

import cn.zswltech.mithras.capital.enums.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.capital.enums.FinancingFlowWriteOffStatusEnum;
import cn.zswltech.mithras.capital.service.model.CapitalBankFlowWriteOffState;
import org.springframework.stereotype.Service;

@Service
public class CapitalBankFlowWriteOffRuleService {

    public CapitalBankFlowWriteOffState resolveStateAfterWithdraw(Long surplusAmount,
                                                                  Double debitAmount,
                                                                  Double creditAmount) {
        long surplus = surplusAmount == null ? 0L : surplusAmount;
        double debit = debitAmount == null ? 0D : debitAmount;
        double credit = creditAmount == null ? 0D : creditAmount;
        double originAmount = (debit + credit) * 10000;

        if (surplus <= 0) {
            return new CapitalBankFlowWriteOffState(
                    FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name(), null);
        }
        if (surplus >= originAmount) {
            return new CapitalBankFlowWriteOffState(
                    FinancingFlowWriteOffStatusEnum.NO_WRITE_OFF.name(),
                    BankFlowCenterTypeEnum.PROCESSING_CENTER.name());
        }
        return new CapitalBankFlowWriteOffState(
                FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name(),
                BankFlowCenterTypeEnum.PROCESSING_CENTER.name());
    }
}
