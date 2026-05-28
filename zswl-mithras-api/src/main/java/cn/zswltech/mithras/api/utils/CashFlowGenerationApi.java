package cn.zswltech.mithras.api.utils;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yibin
 */
@Api(tags = "现金流计划生成-接口")
@RequestMapping("/utils/cashFlow/generation")
public interface CashFlowGenerationApi {

    @ApiOperation("生成现金流计划表/租金概算表")
    @PostMapping("execute")
    R<List<CashFlowGenerationExecRSP>> generate(@RequestBody @Valid CashFlowGenerationExecREQ req);

    @ApiOperation("irr计算")
    @PostMapping("irr")
    R<CashFlowGenerationIrrRSP> irr(@RequestBody @Valid CashFlowGenerationIrrREQ req);

    @ApiOperation("导入现金流计划表/概算租金表")
    @PostMapping("import")
    R<List<CashFlowGenerationExecRSP>> importCashFlow(@RequestParam("file") MultipartFile file);

    @ApiOperation("导出现金流计划表/概算租金表")
    @PostMapping("export")
    R<Void> exportCashFlow(@RequestBody @Valid CashFlowGenerationExportREQ req);

}
