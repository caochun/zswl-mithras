package cn.zswltech.mithras.application.orchestration.adapter.workbench;

import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.workbench.application.WorkbenchFundsLiquidityCollection;
import cn.zswltech.mithras.workbench.application.port.WorkbenchFundsLiquidityPort;
import cn.zswltech.mithras.workbench.application.WorkbenchFundsLiquidityRepayCashFlow;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkbenchFundsLiquidityPortAdapter implements WorkbenchFundsLiquidityPort {
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private FundReceiptRepayCashFlowService repayCashFlowService;

    @Override
    public List<WorkbenchFundsLiquidityCollection> listRentCollections(LocalDate start, LocalDate end) {
        return collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .between(CollectionBaseInfo::getPlanCollectionDate, start, end)
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()))
                .stream()
                .map(collection -> new WorkbenchFundsLiquidityCollection(
                        collection.getPlanCollectionDate(),
                        collection.getPlanCollectionAmount(),
                        collection.getCollectionAmount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkbenchFundsLiquidityRepayCashFlow> listRepayCashFlows(LocalDate start, LocalDate end) {
        return repayCashFlowService.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .le(FundReceiptRepayCashFlow::getRepayDate, end)
                        .ge(FundReceiptRepayCashFlow::getRepayDate, start))
                .stream()
                .map(cashFlow -> new WorkbenchFundsLiquidityRepayCashFlow(
                        cashFlow.getRepayDate(),
                        cashFlow.getRepayAmount(),
                        cashFlow.getWriteOffState()))
                .collect(Collectors.toList());
    }
}
