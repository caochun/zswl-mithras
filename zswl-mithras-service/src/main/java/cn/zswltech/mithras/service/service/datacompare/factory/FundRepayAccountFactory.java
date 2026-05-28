package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.receiptrepay.FundRepayAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundRepayAccount;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundRepayAccountLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl.FundRepayAccountLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayRepayAccount")
public class FundRepayAccountFactory implements EditdataCompareFactory {
    @Resource
    private FundRepayAccountLibMapper libMapper;
    @Resource
    private FundRepayAccountLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundRepayAccount, FundRepayAccountLib, FundRepayAccountListRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.FUND_RECEIPT_REPAY.name(), version);
    }
}
