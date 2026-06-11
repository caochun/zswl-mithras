package cn.zswltech.mithras.fund.application.lib.financing;

import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingRepayEstimateLibMapper;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingRepayEstimate;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingRepayEstimateLib;
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
