package cn.zswltech.mithras.application.orchestration.adapter.liquidity;

import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.application.port.FundParameterConfigSupportPort;
import cn.zswltech.mithras.application.orchestration.liquidity.AccountBalanceBaseInfoService;
import cn.zswltech.mithras.application.orchestration.liquidity.LiquidityDataService;
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
