package cn.zswltech.mithras.service.service.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingRepayEstimateLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayEstimate;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayEstimateLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FundFinancingRepayEstimateLibService extends ServiceImpl<FundFinancingRepayEstimateLibMapper, FundFinancingRepayEstimateLib> {
    public List<FundFinancingRepayEstimateLib> listByFinancingIdVersion(Long financingId, String version) {
        LambdaQueryWrapper<FundFinancingRepayEstimateLib> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingRepayEstimate::getFinancingId, financingId);
        query.eq(FundFinancingRepayEstimateLib::getVersion, version);
        return this.list(query);
    }
}
