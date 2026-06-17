package cn.zswltech.mithras.liquidity.application.port;

import java.time.LocalDateTime;

public interface FundParameterConfigSupportPort {

    LocalDateTime getAccountBalanceUpdateTime();

    void settingDataUpdate();
}
