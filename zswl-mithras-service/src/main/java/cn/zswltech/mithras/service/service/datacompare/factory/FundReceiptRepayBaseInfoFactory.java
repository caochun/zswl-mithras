package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.receiptrepay.FundReceiptRepayBaseInfoLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl.FundReceiptRepayBaseInfoLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayBaseInfo")
public class FundReceiptRepayBaseInfoFactory implements EditdataCompareFactory {
    @Resource
    private FundReceiptRepayBaseInfoLibMapper libMapper;
    @Resource
    private FundReceiptRepayBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundReceiptRepayBaseInfo, FundReceiptRepayBaseInfoLib, FundReceiptRepayBaseInfoDetailRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.FUND_RECEIPT_REPAY.name(), version);
    }
}
