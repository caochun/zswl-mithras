package cn.zswltech.mithras.api.liquiditymanage;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquiditymanage.base.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @author chenyifei
 */
@Api(tags = "流动性管理-基本信息-接口")
public interface FundLiquidityBaseApi {

    @PostMapping(path = "/liquidity/accountBalance/list")
    @ApiOperation(value = "账户余额明细列表")
    R<AccountBalanceDetailListRSP> accountBalanceList(@RequestBody @Valid AccountBalanceDetailListREQ req);

    @PostMapping(path = "/liquidity/accountBalance/modify")
    @ApiOperation(value = "账户余额明细编辑")
    R<AccountBalanceDetailModifyRSP> accountBalanceModify(@RequestBody @Valid AccountBalanceDetailModifyREQ req);

    @PostMapping(path = "/liquidity/accountBalance/import")
    @ApiOperation(value = "账户余额明细导入")
    R<Void> accountBalanceImport(@RequestParam("file") MultipartFile file);

    @PostMapping(path = "/liquidity/accountSetting/list")
    @ApiOperation(value = "回款账户配置列表")
    R<List<AccountSettingListRSP>> accountSettingList(@RequestBody @Valid AccountSettingListREQ req);

    @PostMapping(path = "/liquidity/accountSetting/modify")
    @ApiOperation(value = "回款账户配置编辑")
    R<AccountSettingModifyRSP> accountSettingModify(@RequestBody @Valid AccountSettingModifyREQ req);

    @PostMapping(path = "/liquidity/accountSetting/restore")
    @ApiOperation(value = "回款账户配置账户还原")
    R<AccountSettingRestoreRSP> accountSettingRestore(@RequestBody @Valid AccountSettingRestoreREQ req);

    @PostMapping(path = "/liquidity/setting/parameterBase/detail")
    @ApiOperation(value = "基础参数配置详情")
    R<ParameterBaseDetailRSP> parameterBaseDetail(@RequestBody @Valid ParameterBaseDetailREQ req);

    @PostMapping(path = "/liquidity/setting/parameterBase/modify")
    @ApiOperation(value = "基础参数配置编辑")
    R<ParameterBaseModifyRSP> parameterBaseModify(@RequestBody @Valid ParameterBaseModifyREQ req);

    @PostMapping(path = "/liquidity/setting/parameterIndex/detail")
    @ApiOperation(value = "流动性指标配置详情")
    R<List<ParameterIndexDetailRSP>> parameterIndexDetail(@RequestBody @Valid ParameterIndexDetailREQ req);

    @PostMapping(path = "/liquidity/setting/parameterIndex/modify")
    @ApiOperation(value = "流动性指标配置编辑")
    R<ParameterIndexModifyRSP> parameterIndexModify(@RequestBody @Valid List<ParameterIndexModifyREQ> req);

    @PostMapping("/liquidity/test")
    void test();

    @PostMapping("/liquidity/testSetting")
    void testSetting();


}
