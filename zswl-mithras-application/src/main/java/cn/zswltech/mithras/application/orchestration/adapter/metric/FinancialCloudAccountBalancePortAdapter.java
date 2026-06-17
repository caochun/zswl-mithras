package cn.zswltech.mithras.application.orchestration.adapter.metric;

import cn.zswltech.mithras.liquidity.mapper.AccountBalanceBaseInfoMapper;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.metric.financialcloudmetric.port.FinancialCloudAccountBalancePort;
import cn.zswltech.mithras.metric.financialcloudmetric.port.FinancialCloudAccountBalanceSnapshot;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FinancialCloudAccountBalancePortAdapter implements FinancialCloudAccountBalancePort {

    @Resource
    private AccountBalanceBaseInfoMapper accountBalanceBaseInfoMapper;

    @Override
    public List<FinancialCloudAccountBalanceSnapshot> listByDateRange(LocalDate start, LocalDate end) {
        return accountBalanceBaseInfoMapper.selectList(Wrappers.<AccountBalanceBaseInfo>lambdaQuery()
                        .ge(AccountBalanceBaseInfo::getDate, start)
                        .le(AccountBalanceBaseInfo::getDate, end))
                .stream()
                .map(item -> new FinancialCloudAccountBalanceSnapshot(item.getRentReflowAmount(), item.getRepayAmount(), item.getRepayEditAmount()))
                .collect(Collectors.toList());
    }
}
