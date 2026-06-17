package cn.zswltech.mithras.application.orchestration.adapter.liquidity;

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
import cn.zswltech.mithras.liquidity.service.FundLiquidityBaseApplicationService;
import cn.zswltech.mithras.liquidity.service.FundParameterConfigService;
import cn.zswltech.mithras.application.orchestration.liquidity.AccountBalanceBaseInfoService;
import cn.zswltech.mithras.application.orchestration.liquidity.FundFinancingAccountSettingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@Service
public class FundLiquidityBaseApplicationAdapter implements FundLiquidityBaseApplicationService {

    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;
    @Resource
    private FundFinancingAccountSettingService accountSettingService;
    @Resource
    private FundParameterConfigService parameterConfigService;

    @Override
    public AccountBalanceDetailListRSP accountBalanceList(AccountBalanceDetailListREQ req) {
        return accountBalanceBaseInfoService.accountBalanceList(req);
    }

    @Override
    public AccountBalanceDetailModifyRSP accountBalanceModify(AccountBalanceDetailModifyREQ req) {
        return accountBalanceBaseInfoService.accountBalanceModify(req);
    }

    @Override
    public void importAccountBalance(InputStream inputStream) {
        accountBalanceBaseInfoService.importExcel(inputStream);
    }

    @Override
    public List<AccountSettingListRSP> accountSettingList(AccountSettingListREQ req) {
        return accountSettingService.accountSettingList(req);
    }

    @Override
    public AccountSettingModifyRSP accountSettingModify(AccountSettingModifyREQ req) {
        return accountSettingService.accountSettingModify(req);
    }

    @Override
    public AccountSettingRestoreRSP accountSettingRestore(AccountSettingRestoreREQ req) {
        return accountSettingService.accountSettingRestore(req);
    }

    @Override
    public ParameterBaseDetailRSP parameterBaseDetail(ParameterBaseDetailREQ req) {
        return parameterConfigService.parameterBaseDetail(req);
    }

    @Override
    public ParameterBaseModifyRSP parameterBaseModify(ParameterBaseModifyREQ req) {
        return parameterConfigService.parameterBaseModify(req);
    }

    @Override
    public List<ParameterIndexDetailRSP> parameterIndexDetail(ParameterIndexDetailREQ req) {
        return parameterConfigService.parameterIndexDetail(req);
    }

    @Override
    public ParameterIndexModifyRSP parameterIndexModify(List<ParameterIndexModifyREQ> req) {
        return parameterConfigService.parameterIndexModify(req);
    }

    @Override
    public void accountBalanceCalculate() {
        accountBalanceBaseInfoService.init(false, null);
    }

    @Override
    public void accountSettingCalculate() {
        accountSettingService.init();
    }
}
