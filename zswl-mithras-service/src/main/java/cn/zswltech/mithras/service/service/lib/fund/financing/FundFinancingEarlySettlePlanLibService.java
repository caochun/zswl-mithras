package cn.zswltech.mithras.service.service.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingEarlySettlePlanLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingEarlySettlePlan;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingEarlySettlePlanLib;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FundFinancingEarlySettlePlanLibService extends ServiceImpl<FundFinancingEarlySettlePlanLibMapper, FundFinancingEarlySettlePlanLib> {
    public FundFinancingEarlySettlePlanLib getOneByFinancingIdVersion(Long financingId, String version) {
        LambdaQueryWrapper<FundFinancingEarlySettlePlanLib> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingEarlySettlePlan::getFinancingId, financingId);
        query.eq(FundFinancingEarlySettlePlanLib::getVersion, version);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }
}
