package cn.zswltech.mithras.liquiditymanage.service;

import java.time.LocalDateTime;

public interface FundParameterConfigSupportPort {

    LocalDateTime getAccountBalanceUpdateTime();

    void settingDataUpdate();
}
