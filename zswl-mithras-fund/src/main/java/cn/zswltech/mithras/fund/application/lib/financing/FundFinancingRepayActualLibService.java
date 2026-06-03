package cn.zswltech.mithras.fund.application.lib.financing;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing.FundFinancingRepayActualLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingRepayActualLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FundFinancingRepayActualLibService extends ServiceImpl<FundFinancingRepayActualLibMapper, FundFinancingRepayActualLib> {
    public List<FundFinancingRepayActualLib> listByFinancingIdVersion(Long financingId, String version) {
        LambdaQueryWrapper<FundFinancingRepayActualLib> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingRepayActual::getFinancingId, financingId);
        query.eq(FundFinancingRepayActualLib::getVersion, version);
        return this.list(query);
    }
}
