package cn.zswltech.mithras.service.service.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingPlanLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.service.util.StringUtil;
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
