package cn.zswltech.mithras.fund.application.lib.financing;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing.FundFinancingPledgeInfoLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfoLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FundFinancingPledgeInfoLibService extends ServiceImpl<FundFinancingPledgeInfoLibMapper, FundFinancingPledgeInfoLib> {
    public List<FundFinancingPledgeInfoLib> listByFinancingIdVersion(Long financingId, String version) {
        LambdaQueryWrapper<FundFinancingPledgeInfoLib> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingPledgeInfo::getFinancingId, financingId);
        query.eq(FundFinancingPledgeInfoLib::getVersion, version);
        return this.list(query);
    }

    public FundFinancingPledgeInfoLib getByContractId(Long contractId) {
        List<FundFinancingPledgeInfoLib> list = baseMapper.getLastestByContractId(contractId);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
