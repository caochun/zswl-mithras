package cn.zswltech.mithras.api.capital;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.write_off.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/19 18:45
 * @description
 */
@Api(tags = "自动核销-API")
@RequestMapping(path = "/capital/write/off")
public interface CapitalWriteOffApi {

    @ApiOperation(value = "手动核销")
    @PostMapping(path = "/manual/write/off")
    R<Void> manualWriteOff(@RequestBody @Valid ManualWriteOffREQ req);

    @ApiOperation(value = "选择流水后的校验")
    @PostMapping(path = "/check/before/import")
    R<CheckBeforeImportRSP> checkBeforeImport(@RequestBody @Valid CheckBeforeImportREQ req);

    @ApiOperation(value = "选择流水之后的匹配结果")
    @PostMapping(path = "/flow/match/result")
    R<List<FlowMatchResultRSP>> flowMatchResult(@RequestBody @Valid CheckBeforeImportREQ req);

    @ApiOperation(value = "删除银行流水")
    @PostMapping(path = "/delete/bank/flow")
    R<Void> deleteBankFlow(@RequestBody @Valid DeleteFlowREQ req);

    @ApiOperation(value = "增加银行流水")
    @PostMapping(path = "/add/bank/flow")
    R<Void> addBankFlow(@RequestBody @Valid AddFlowREQ req);

    @ApiOperation(value = "修改业务流水")
    @PostMapping(path = "/update/business/flow")
    R<Void> updateBusinessFlow(@RequestBody @Valid UpdateBusinessFlowREQ req);

    @ApiOperation(value = "新增业务流水")
    @PostMapping(path = "/add/business/flow")
    R<Void> addBusinessFlow(@RequestBody @Valid AddBusinessFlowREQ req);

    @ApiOperation(value = "删除业务流水")
    @PostMapping(path = "/delete/business/flow")
    R<Void> deleteBusinessFlow(@RequestBody @Valid DeleteBusinessFlowREQ req);

    @ApiOperation(value = "重新匹配单个Tab的信息")
    @PostMapping(path = "/rematch/tab")
    R<Void> rematchTab(@RequestBody @Valid RematchTabREQ req);

    @ApiOperation(value = "删除单个Tab")
    @PostMapping(path = "/delete/tab")
    R<Void> deleteTab(@RequestBody @Valid DeleteTabREQ req);

    @ApiOperation(value = "获取单个Tab信息")
    @PostMapping(path = "/single/tab")
    R<FlowMatchResultRSP> singleTab(@RequestBody @Valid SingleTabREQ req);

    @ApiOperation(value = "释放银行流水")
    @PostMapping(path = "/release/bank/flow")
    R<Void> releaseBankFlow(@RequestBody @Valid ReleaseBankFlowREQ req);
}
