package cn.zswltech.mithras.service.controller.liquiditymanage;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.liquiditymanage.FundLiquidityBaseApi;
import cn.zswltech.mithras.dto.liquiditymanage.base.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.liquiditymanage.AccountBalanceBaseInfoService;
import cn.zswltech.mithras.service.service.liquiditymanage.FundFinancingAccountSettingService;
import cn.zswltech.mithras.service.service.liquiditymanage.FundParameterConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * AccountBalanceBaseInfoController
 *
 * @author chenyifei
 * @since 2024/12/13
 */
@RestController
@Slf4j
public class FundLiquidityBaseController implements FundLiquidityBaseApi {

    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;
    @Resource
    private FundFinancingAccountSettingService accountSettingService;
    @Resource
    private FundParameterConfigService parameterConfigService;

    @Override
    public R<AccountBalanceDetailListRSP> accountBalanceList(AccountBalanceDetailListREQ req) {
        return R.ok(accountBalanceBaseInfoService.accountBalanceList(req));
    }

    @Override
    public R<AccountBalanceDetailModifyRSP> accountBalanceModify(AccountBalanceDetailModifyREQ req) {
        return R.ok(accountBalanceBaseInfoService.accountBalanceModify(req));
    }

    @Override
    public R<Void> accountBalanceImport(MultipartFile file) {
        Assert.isTrue(!file.isEmpty(), () -> MithrasException.newException("文件不能为空"));
        try {
            accountBalanceBaseInfoService.importExcel(file.getInputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入账户余额表发生未知异常", e);
            return R.fail("导入实际还款表发生未知异常");
        }
        return R.ok();
    }

    @Override
    public R<List<AccountSettingListRSP>> accountSettingList(AccountSettingListREQ req) {
        return R.ok(accountSettingService.accountSettingList(req));
    }

    @Override
    public R<AccountSettingModifyRSP> accountSettingModify(AccountSettingModifyREQ req) {
        return R.ok(accountSettingService.accountSettingModify(req));
    }

    @Override
    public R<AccountSettingRestoreRSP> accountSettingRestore(AccountSettingRestoreREQ req) {
        return R.ok(accountSettingService.accountSettingRestore(req));
    }

    @Override
    public R<ParameterBaseDetailRSP> parameterBaseDetail(ParameterBaseDetailREQ req) {
        return R.ok(parameterConfigService.parameterBaseDetail(req));
    }

    @Override
    public R<ParameterBaseModifyRSP> parameterBaseModify(ParameterBaseModifyREQ req) {
        return R.ok(parameterConfigService.parameterBaseModify(req));
    }

    @Override
    public R<List<ParameterIndexDetailRSP>> parameterIndexDetail(ParameterIndexDetailREQ req) {
        return R.ok(parameterConfigService.parameterIndexDetail(req));
    }

    @Override
    public R<ParameterIndexModifyRSP> parameterIndexModify(List<ParameterIndexModifyREQ> req) {
        return R.ok(parameterConfigService.parameterIndexModify(req));
    }

    @Override
    public void test() {
        accountBalanceBaseInfoService.init(false, null);
    }

    @Override
    public void testSetting() {
        accountSettingService.init();
    }
}
