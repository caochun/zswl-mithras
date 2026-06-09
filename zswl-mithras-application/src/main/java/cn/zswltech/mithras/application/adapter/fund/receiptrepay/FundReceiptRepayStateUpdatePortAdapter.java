package cn.zswltech.mithras.application.adapter.fund.receiptrepay;

import cn.zswltech.mithras.fund.application.receiptrepay.port.FundReceiptRepayStateUpdatePort;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayStateService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FundReceiptRepayStateUpdatePortAdapter implements FundReceiptRepayStateUpdatePort {

    @Resource
    private FundReceiptRepayStateService fundReceiptRepayStateService;

    @Override
    public void modifyUpdateProcessState(Long receiptRepayId) {
        fundReceiptRepayStateService.modifyUpdateProcessState(receiptRepayId);
    }
}
