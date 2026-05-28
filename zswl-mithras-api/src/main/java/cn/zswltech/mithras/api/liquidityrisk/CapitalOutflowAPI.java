package cn.zswltech.mithras.api.liquidityrisk;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author wwj
 * @create: 2023-05-15
 **/

@Api(tags = "流动性风险-资金流出-接口")
public interface CapitalOutflowAPI {

    @ApiOperation("现金流流出明细-资金端")
    @PostMapping("/funds/cash/outflow/list")
    R<FundsCashOutflowListRsp> fundsCashOutflowList(@RequestBody @Valid CashOutflowListReq req);

    @ApiOperation("现金流流出明细-资产端")
    @PostMapping("/assets/cash/outflow/list")
    R<AssetsCashOutflowListRsp> assetsCashOutflowList(@RequestBody @Valid CashOutflowListReq req);

    @ApiOperation("预估现金流流出")
    @PostMapping("/estimate/cash/outflow/list")
    R<ChartQueryRSP> estimateCashOutflowList(@RequestBody @Valid CashOutflowListReq req);

    @ApiOperation("压力测试-流出")
    @PostMapping("/stress/testing/outflow/list")
    R<ChartQueryRSP> stressTestingOutflowList(@RequestBody @Valid CashOutflowListReq req);

    @ApiOperation("现金流流出明细-导出")
    @PostMapping("/cash/outflow/export")
    R<Void> cashOutflowExport(@RequestBody @Valid CashOutflowListReq req);

    @ApiOperation("流动性统计")
    @PostMapping("/cash/inOut/stat")
    R<List<CashInOutStatRSP>> cashInOutStat(@RequestBody @Valid CashInOutStatREQ req);

    @ApiOperation("流动性统计下载")
    @PostMapping("/cash/inOut/stat/download")
    void downloadInOutStat(@RequestBody @Valid CashInOutStatREQ req);
}
