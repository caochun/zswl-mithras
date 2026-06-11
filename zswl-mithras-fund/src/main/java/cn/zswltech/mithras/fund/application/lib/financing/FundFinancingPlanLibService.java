package cn.zswltech.mithras.fund.application.lib.financing;

import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingPlanLibMapper;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FundFinancingPlanLibService extends ServiceImpl<FundFinancingPlanLibMapper, FundFinancingPlanLib> {
    public FundFinancingPlanLib getOneByFinancingIdVersion(Long financingId, String version) {
        LambdaQueryWrapper<FundFinancingPlanLib> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingPlan::getFinancingId, financingId);
        query.eq(FundFinancingPlanLib::getVersion, version);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public FundFinancingPlanLib getLatestByFinancingIdVersion(Long financingId, String version) {
        LambdaQueryWrapper<FundFinancingPlanLib> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingPlan::getFinancingId, financingId);
        query.lt(FundFinancingPlanLib::getVersion, version);
        query.orderByDesc(FundFinancingPlanLib::getVersion);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public List<FundFinancingPlanLib> queryLastestVersionLibs(Set<Long> financingIds) {
        return baseMapper.queryLastestVersionLibs(financingIds);
    }
}
