package cn.zswltech.mithras.fund.application.lib.financing;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing.FundFinancingCollectAccountLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingCollectAccountLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FundFinancingCollectAccountLibService extends ServiceImpl<FundFinancingCollectAccountLibMapper, FundFinancingCollectAccountLib> {
    public List<FundFinancingCollectAccountLib> getVersionList(Long financingId, String financingVersion) {
        return baseMapper.selectList(Wrappers.<FundFinancingCollectAccountLib>lambdaQuery()
                .eq(FundFinancingCollectAccountLib::getFinancingId, financingId)
                .eq(FundFinancingCollectAccountLib::getVersion, financingVersion));
    }
}
