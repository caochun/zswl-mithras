package cn.zswltech.mithras.api.fund.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-实际还款表
 * @date 2023-06-17
 */
@Api(tags = "直接融资-实际还款表-接口")
public interface FundDirectFinancingRepayActualApi {

    @ApiOperation("直接融资-实际还款表-导入")
    @PostMapping("/fund/direct/financing/repay/actual/import")
    R<FundDirectFinancingRepayActualImportRSP> importExcel(@Valid FundDirectFinancingRepayActualImportREQ req);

    @ApiOperation("直接融资-实际还款表-导出")
    @PostMapping("/fund/direct/financing/repay/actual/modify")
    void exportExcel(@RequestBody @Valid FundDirectFinancingRepayActualExportREQ req);

    @ApiOperation("直接融资-实际还款表-列表")
    @PostMapping("/fund/direct/financing/repay/actual/list")
    R<PageR<FundDirectFinancingRepayActualListRSP>> list(@RequestBody @Valid FundDirectFinancingRepayActualListREQ req);

    @ApiOperation("直接融资-实际还款表-拆分明细列表")
    @PostMapping("/fund/direct/financing/repay/actual/split/list")
    R<List<FundDirectRepayActualSplitRSP>> splitList(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("测算")
    @PostMapping(path = "/fund/direct/financing/repay/actual/list/calculate")
    R<Void> calculate(@RequestBody @Valid SingleFinancingIdREQ req);

}