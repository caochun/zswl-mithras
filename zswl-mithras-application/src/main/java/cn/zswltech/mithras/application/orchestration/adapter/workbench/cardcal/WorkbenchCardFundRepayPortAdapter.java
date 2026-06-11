package cn.zswltech.mithras.application.orchestration.adapter.workbench.cardcal;

import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.workbench.application.cardcal.WorkbenchCardFundRepayPort;
import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchFundRepayCashFlow;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkbenchCardFundRepayPortAdapter implements WorkbenchCardFundRepayPort {
    @Resource
    private FundReceiptRepayCashFlowService receiptRepayCashFlowService;

    @Override
    public List<WorkbenchFundRepayCashFlow> listRepayCashFlows(LocalDate start, LocalDate end) {
        return receiptRepayCashFlowService.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .ge(FundReceiptRepayCashFlow::getRepayDate, start)
                        .le(FundReceiptRepayCashFlow::getRepayDate, end))
                .stream()
                .map(cashFlow -> new WorkbenchFundRepayCashFlow(
                        cashFlow.getWriteOffState(),
                        cashFlow.getRepayAmount()))
                .collect(Collectors.toList());
    }
}
