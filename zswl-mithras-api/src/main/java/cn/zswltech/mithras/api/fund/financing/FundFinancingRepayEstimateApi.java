package cn.zswltech.mithras.api.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualImportRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayImportREQ;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayEstimateListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Api(tags = "融资管理-还款概算表相关接口")
@RequestMapping(path = "/fund/financing/repay/estimate")
public interface FundFinancingRepayEstimateApi {
    @ApiOperation("还款概算表列表")
    @PostMapping("/list")
    R<List<FundFinancingRepayEstimateListRSP>> list(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("导入还款概算表")
    @PostMapping(path = "/import")
    R<FundFinancingRepayActualImportRSP> importExcel(@Valid FundFinancingRepayImportREQ req);

    @ApiOperation("导出还款概算表")
    @PostMapping(path = "/export")
    void exportExcel(@RequestBody @Valid SingleFinancingIdREQ req);
}
