package cn.zswltech.mithras.liquidity.service;

import java.time.LocalDateTime;

public interface FundParameterConfigSupportPort {

    LocalDateTime getAccountBalanceUpdateTime();

    void settingDataUpdate();
}
