package cn.zswltech.mithras.application.orchestration.adapter.fund.receiptrepay;

import cn.zswltech.mithras.fund.application.receiptrepay.port.FundReceiptFlowDetailAmountPort;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowDetailService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FundReceiptFlowDetailAmountPortAdapter implements FundReceiptFlowDetailAmountPort {

    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;

    @Override
    public long sum(Long receiptRepayId, String cashFlowCode) {
        return fundReceiptFlowDetailService.sum(receiptRepayId, cashFlowCode);
    }
}
