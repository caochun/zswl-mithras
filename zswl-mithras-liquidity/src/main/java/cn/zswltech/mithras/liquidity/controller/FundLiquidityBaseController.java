package cn.zswltech.mithras.liquidity.controller;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.liquiditymanage.FundLiquidityBaseApi;
import cn.zswltech.mithras.dto.liquiditymanage.base.*;
import cn.zswltech.mithras.liquidity.service.FundLiquidityBaseApplicationService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
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
    private FundLiquidityBaseApplicationService fundLiquidityBaseService;

    @Override
    public R<AccountBalanceDetailListRSP> accountBalanceList(AccountBalanceDetailListREQ req) {
        return R.ok(fundLiquidityBaseService.accountBalanceList(req));
    }

    @Override
    public R<AccountBalanceDetailModifyRSP> accountBalanceModify(AccountBalanceDetailModifyREQ req) {
        return R.ok(fundLiquidityBaseService.accountBalanceModify(req));
    }

    @Override
    public R<Void> accountBalanceImport(MultipartFile file) {
        Assert.isTrue(!file.isEmpty(), () -> MithrasException.newException("文件不能为空"));
        try {
            fundLiquidityBaseService.importAccountBalance(file.getInputStream());
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
        return R.ok(fundLiquidityBaseService.accountSettingList(req));
    }

    @Override
    public R<AccountSettingModifyRSP> accountSettingModify(AccountSettingModifyREQ req) {
        return R.ok(fundLiquidityBaseService.accountSettingModify(req));
    }

    @Override
    public R<AccountSettingRestoreRSP> accountSettingRestore(AccountSettingRestoreREQ req) {
        return R.ok(fundLiquidityBaseService.accountSettingRestore(req));
    }

    @Override
    public R<ParameterBaseDetailRSP> parameterBaseDetail(ParameterBaseDetailREQ req) {
        return R.ok(fundLiquidityBaseService.parameterBaseDetail(req));
    }

    @Override
    public R<ParameterBaseModifyRSP> parameterBaseModify(ParameterBaseModifyREQ req) {
        return R.ok(fundLiquidityBaseService.parameterBaseModify(req));
    }

    @Override
    public R<List<ParameterIndexDetailRSP>> parameterIndexDetail(ParameterIndexDetailREQ req) {
        return R.ok(fundLiquidityBaseService.parameterIndexDetail(req));
    }

    @Override
    public R<ParameterIndexModifyRSP> parameterIndexModify(List<ParameterIndexModifyREQ> req) {
        return R.ok(fundLiquidityBaseService.parameterIndexModify(req));
    }

    @Override
    public void test() {
        fundLiquidityBaseService.accountBalanceCalculate();
    }

    @Override
    public void testSetting() {
        fundLiquidityBaseService.accountSettingCalculate();
    }
}
