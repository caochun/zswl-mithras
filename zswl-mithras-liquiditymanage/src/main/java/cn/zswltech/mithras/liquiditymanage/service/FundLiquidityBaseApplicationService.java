package cn.zswltech.mithras.liquiditymanage.service;

import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailModifyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountSettingListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountSettingListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountSettingModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountSettingModifyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountSettingRestoreREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountSettingRestoreRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseModifyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexModifyRSP;

import java.io.InputStream;
import java.util.List;

public interface FundLiquidityBaseApplicationService {

    AccountBalanceDetailListRSP accountBalanceList(AccountBalanceDetailListREQ req);

    AccountBalanceDetailModifyRSP accountBalanceModify(AccountBalanceDetailModifyREQ req);

    void importAccountBalance(InputStream inputStream);

    List<AccountSettingListRSP> accountSettingList(AccountSettingListREQ req);

    AccountSettingModifyRSP accountSettingModify(AccountSettingModifyREQ req);

    AccountSettingRestoreRSP accountSettingRestore(AccountSettingRestoreREQ req);

    ParameterBaseDetailRSP parameterBaseDetail(ParameterBaseDetailREQ req);

    ParameterBaseModifyRSP parameterBaseModify(ParameterBaseModifyREQ req);

    List<ParameterIndexDetailRSP> parameterIndexDetail(ParameterIndexDetailREQ req);

    ParameterIndexModifyRSP parameterIndexModify(List<ParameterIndexModifyREQ> req);

    void accountBalanceCalculate();

    void accountSettingCalculate();
}
