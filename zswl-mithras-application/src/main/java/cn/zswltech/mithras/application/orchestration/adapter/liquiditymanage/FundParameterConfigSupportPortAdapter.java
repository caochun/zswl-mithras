package cn.zswltech.mithras.application.orchestration.adapter.liquiditymanage;

import cn.zswltech.mithras.liquidity.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.service.FundParameterConfigSupportPort;
import cn.zswltech.mithras.application.orchestration.liquiditymanage.AccountBalanceBaseInfoService;
import cn.zswltech.mithras.application.orchestration.liquiditymanage.LiquidityDataService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FundParameterConfigSupportPortAdapter implements FundParameterConfigSupportPort {

    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;
    @Resource
    private LiquidityDataService liquidityDataService;

    @Override
    public LocalDateTime getAccountBalanceUpdateTime() {
        AccountBalanceBaseInfo maxTimeAccountBalance = accountBalanceBaseInfoService.getOne(Wrappers.<AccountBalanceBaseInfo>lambdaQuery()
                .select(AccountBalanceBaseInfo::getUpdateTime)
                .isNotNull(AccountBalanceBaseInfo::getActualBalanceAmount)
                .orderByDesc(AccountBalanceBaseInfo::getUpdateTime)
                .last(StringUtil.mysqlLimitOne()));
        return Optional.ofNullable(maxTimeAccountBalance).map(AccountBalanceBaseInfo::getUpdateTime).orElse(null);
    }

    @Override
    public void settingDataUpdate() {
        liquidityDataService.settingDataUpdate();
    }
}
