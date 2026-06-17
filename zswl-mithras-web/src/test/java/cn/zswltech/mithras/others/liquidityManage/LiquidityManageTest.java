package cn.zswltech.mithras.others.liquidityManage;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.application.orchestration.liquidity.AccountBalanceBaseInfoService;
import cn.zswltech.mithras.application.orchestration.liquidity.FundFinancingAccountSettingService;
import cn.zswltech.mithras.application.orchestration.liquidity.LiquidityDataService;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@ActiveProfiles(value = "dev")
public class LiquidityManageTest extends ApplicationTest  {

    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;
    @Resource
    private FundFinancingAccountSettingService accountSettingService;
    @Resource
    private LiquidityDataService liquidityDataService;

    @Test
    public void test(){
        liquidityDataService.dataQueryAccount();
//        accountBalanceBaseInfoService.init(false);
        accountBalanceBaseInfoService.cal(null);

    }

    @Test
    public void settingInit(){
        accountSettingService.init();
    }



}
