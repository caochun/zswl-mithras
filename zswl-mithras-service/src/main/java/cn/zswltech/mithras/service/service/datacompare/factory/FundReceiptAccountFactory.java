package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.receiptrepay.FundReceiptAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptAccount;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptAccountLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl.FundReceiptAccountLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayReceiptAccount")
public class FundReceiptAccountFactory implements EditdataCompareFactory {
    @Resource
    private FundReceiptAccountLibMapper libMapper;
    @Resource
    private FundReceiptAccountLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundReceiptAccount, FundReceiptAccountLib, FundReceiptAccountListRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.FUND_RECEIPT_REPAY.name(), version);
    }
}
