package cn.zswltech.mithras.api.liquidityrisk;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 流动性风险-接口
 * @author: jackerhe
 * @date: 2023/5/16 9:34 上午
 **/
@Api(tags = "流动性风险-接口")
public interface CapitalInflowApi {

    /**
     * 流入明细查询
     *
     * @param req
     * @return
     */
    @ApiOperation("流入明细查询")
    @PostMapping("/liquidity/risk/asset/inflow/detail")
    R<AssetInflowDetailRSP> inFlowDetail(@RequestBody @Valid AssetInflowDetailREQ req);

    /**
     * 流入明细下载
     * @author: jackerhe
     * @date: 2023/5/15 5:45 下午
     **/
    @ApiOperation("流入明细下载")
    @GetMapping("/liquidity/risk/asset/inflow/download")
    R<Void> inFlowDetailDownload(@Valid AssetInflowDetailREQ req);

    /**
     * 短期贷款明细查询
     * @author: jackerhe
     * @date: 2023/5/15 5:45 下午
     **/
    @ApiOperation("短期贷款明细查询")
    @PostMapping("/liquidity/risk/short/term/loan/detail")
    R<ShortTermLoanDetailRSP> shortTermLoanDetail(@RequestBody @Valid ShortTermLoanDetailREQ req);

    /**
     * 短期贷款明细下载
     * @author: jackerhe
     * @date: 2023/5/15 5:45 下午
     **/
    @ApiOperation("短期贷款明细下载")
    @GetMapping("/liquidity/risk/short/term/loan/download")
    R<Void> shortTermLoanDownload(@Valid ShortTermLoanDetailREQ req);

    /**
     * 图表-折线图 柱状图查询
     **/
    @ApiOperation("图表-折线图 柱状图查询")
    @PostMapping("/liquidity/risk/chart/query")
    R<List<ChartQueryRSP>> chartQuery(@RequestBody @Valid ChartQueryREQ req);

}
