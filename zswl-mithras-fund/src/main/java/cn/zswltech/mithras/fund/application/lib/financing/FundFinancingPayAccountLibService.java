package cn.zswltech.mithras.fund.application.lib.financing;

import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingPayAccountLibMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPayAccountLib;
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
