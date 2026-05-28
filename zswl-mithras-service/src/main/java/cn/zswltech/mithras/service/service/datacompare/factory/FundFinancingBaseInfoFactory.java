package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl.FundFinancingBaseInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/2/24 10:52 上午
 **/
@Service("fundFinancingBaseInfo")
public class FundFinancingBaseInfoFactory implements EditdataCompareFactory {
    @Resource
    private FundFinancingBaseInfoLibMapper libMapper;
    @Resource
    private FundFinancingBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundFinancingBaseInfo, FundFinancingBaseInfoLib, FundFinancingBaseInfoDetailRSP>(rsps, libMapper, handler,
                commonVersionMapper, BusinessModuleEnum.FUND_FINANCING.name(), version);
    }
}