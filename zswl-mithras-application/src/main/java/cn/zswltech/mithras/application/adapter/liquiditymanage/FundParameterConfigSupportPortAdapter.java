package cn.zswltech.mithras.application.adapter.liquiditymanage;

import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquiditymanage.service.FundParameterConfigSupportPort;
import cn.zswltech.mithras.service.service.liquiditymanage.AccountBalanceBaseInfoService;
import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityDataService;
import cn.zswltech.mithras.service.util.StringUtil;
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
