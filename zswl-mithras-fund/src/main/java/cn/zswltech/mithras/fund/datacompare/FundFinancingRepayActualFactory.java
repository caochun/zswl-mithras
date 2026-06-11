package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingRepayActualLibMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.model.financing.FundFinancingRepayActualLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.versioning.financing.handler.impl.FundFinancingRepayActualLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-27
 **/
@Service("fundFinancingRepayActual")
public class FundFinancingRepayActualFactory implements EditdataCompareFactory {
    @Resource
    private FundFinancingRepayActualLibMapper libMapper;
    @Resource
    private FundFinancingRepayActualLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundFinancingRepayActual, FundFinancingRepayActualLib, FundFinancingRepayActualListRSP>(rsps, libMapper, handler,
                commonVersionMapper, "FUND_FINANCING", version);
    }
}