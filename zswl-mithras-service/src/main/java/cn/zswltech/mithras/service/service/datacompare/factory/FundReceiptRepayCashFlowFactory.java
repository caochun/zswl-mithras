package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.receiptrepay.FundReceiptRepayCashFlowLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlowLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl.FundReceiptRepayCashFlowLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayCashFlow")
public class FundReceiptRepayCashFlowFactory implements EditdataCompareFactory {
    @Resource
    private FundReceiptRepayCashFlowLibMapper libMapper;
    @Resource
    private FundReceiptRepayCashFlowLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundReceiptRepayCashFlow, FundReceiptRepayCashFlowLib, FundReceiptRepayCashFlowListRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.FUND_RECEIPT_REPAY.name(), version);
    }
}
