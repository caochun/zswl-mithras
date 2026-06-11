package cn.zswltech.mithras.application.orchestration.facade.liquidityrisk;

import cn.zswltech.mithras.liquidity.application.liquidityrisk.CapitalInflowApplicationService;
import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.liquidity.application.auth.LiquidityRiskViewMainAuthChecker;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.liquidity.application.liquidityrisk.CapitalInflowService;
import cn.zswltech.mithras.application.orchestration.liquidityrisk.CapitalOutflowService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName LiquidityRiskController
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/5/16 10:21 上午
 * @Version 1.0
 **/
@Service
@Slf4j
public class CapitalInflowFacade implements CapitalInflowApplicationService {

    @Resource
    private CapitalInflowService capitalInflowService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private CapitalOutflowService capitalOutflowService;

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<AssetInflowDetailRSP> inFlowDetail(@Valid AssetInflowDetailREQ req) {
        return R.ok(capitalInflowService.inFlowDetail(req));
    }

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<Void> inFlowDetailDownload(@Valid AssetInflowDetailREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("现金流流入明细" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            capitalInflowService.inFlowDetailDownload(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出现金流流入明细表发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出现金流流入明细表发生未知异常");
        }
        return R.ok();
    }

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<ShortTermLoanDetailRSP> shortTermLoanDetail(@Valid ShortTermLoanDetailREQ req) {
        return R.ok(capitalInflowService.shortTermLoanDetail(req));
    }

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<Void> shortTermLoanDownload(@Valid ShortTermLoanDetailREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("短期贷款明细" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            capitalInflowService.shortTermLoanDownload(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出短期贷款明细表发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出短期贷款明细发生未知异常");
        }
        return R.ok();
    }

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<List<ChartQueryRSP>> chartQuery(@Valid ChartQueryREQ req) {
        StopWatch stopWatch = new StopWatch("流动性风险-图表-折线图");
        stopWatch.start("流入图表");
        //预计现金流流入图表
        ChartQueryRSP expectedCashFlowInflowChart = capitalInflowService.expectedCashFlowInflowChart(req);
        //压力测试流入图表
        ChartQueryRSP pressureTestInflowChart = capitalInflowService.pressureTestInflowChart(req);
        //清理数据
        capitalInflowService.threadRemove();
        stopWatch.stop();
        //流出相关
        CashOutflowListReq outflowListReq = new CashOutflowListReq();
        outflowListReq.setTimeFrom(req.getTimeFrom());
        outflowListReq.setTimeTo(req.getTimeTo());
        outflowListReq.setPage(req.getPage());
        outflowListReq.setPageSize(req.getPageSize());
        stopWatch.start("流出图表");
        ChartQueryRSP estimateCashOutflowList = capitalOutflowService.estimateCashOutflowList(outflowListReq);
        ChartQueryRSP stressTestingOutflowList = capitalOutflowService.stressTestingOutflowList(outflowListReq);
        List<ChartQueryRSP> chartQueryRSPS = new ArrayList<>();
        //流入
        chartQueryRSPS.add(expectedCashFlowInflowChart);
        chartQueryRSPS.add(pressureTestInflowChart);
        //流出
        chartQueryRSPS.add(estimateCashOutflowList);
        chartQueryRSPS.add(stressTestingOutflowList);
        stopWatch.stop();
        stopWatch.start("柱状图");
        //柱状图
        Map<String, Long> expectedCashFlowBarMap = expectedCashFlowInflowChart.getDetails().stream().collect(Collectors.toMap(ChartQueryRSP.ChartQueryDetail::getName,
                ChartQueryRSP.ChartQueryDetail::getValue));
        Map<String, Long> pressureTestFlowBarMap = pressureTestInflowChart.getDetails().stream().collect(Collectors.toMap(ChartQueryRSP.ChartQueryDetail::getName,
                ChartQueryRSP.ChartQueryDetail::getValue));
        ChartQueryRSP expectedCashFlowBar = new ChartQueryRSP();
        expectedCashFlowBar.setDataType("预估净现金流");
        expectedCashFlowBar.setChartType("bar");
        expectedCashFlowBar.setDetails(estimateCashOutflowList.getDetails().stream().map(outDetail -> {
            ChartQueryRSP.ChartQueryDetail chartQueryDetail = new ChartQueryRSP.ChartQueryDetail();
            chartQueryDetail.setName(outDetail.getName());
            chartQueryDetail.setValue(LongUtil.null2zero(expectedCashFlowBarMap.get(outDetail.getName())) - LongUtil.null2zero(outDetail.getValue()));
            return chartQueryDetail;
        }).collect(Collectors.toList()));
        ChartQueryRSP pressureTestFlowBar = new ChartQueryRSP();
        pressureTestFlowBar.setDataType("压力测试现金流");
        pressureTestFlowBar.setChartType("bar");
        pressureTestFlowBar.setDetails(stressTestingOutflowList.getDetails().stream().map(outDetail -> {
            ChartQueryRSP.ChartQueryDetail chartQueryDetail = new ChartQueryRSP.ChartQueryDetail();
            chartQueryDetail.setName(outDetail.getName());
            chartQueryDetail.setValue(LongUtil.null2zero(pressureTestFlowBarMap.get(outDetail.getName())) - LongUtil.null2zero(outDetail.getValue()));
            return chartQueryDetail;
        }).collect(Collectors.toList()));
        chartQueryRSPS.add(expectedCashFlowBar);
        chartQueryRSPS.add(pressureTestFlowBar);
        stopWatch.stop();
        log.info("CapitalInflowController chartQuery {}", stopWatch.prettyPrint(TimeUnit.SECONDS));
        return R.ok(chartQueryRSPS);
    }
}
