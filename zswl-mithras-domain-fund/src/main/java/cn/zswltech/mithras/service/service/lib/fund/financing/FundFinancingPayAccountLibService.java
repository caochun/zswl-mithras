package cn.zswltech.mithras.service.service.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingPayAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPayAccountLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FundFinancingPayAccountLibService extends ServiceImpl<FundFinancingPayAccountLibMapper, FundFinancingPayAccountLib> {

    public List<FundFinancingPayAccountLib> getVersionList(Long financingId, String version) {
        return baseMapper.selectList(Wrappers.<FundFinancingPayAccountLib>lambdaQuery()
                .eq(FundFinancingPayAccountLib::getFinancingId, financingId)
                .eq(FundFinancingPayAccountLib::getVersion, version));
    }
}
