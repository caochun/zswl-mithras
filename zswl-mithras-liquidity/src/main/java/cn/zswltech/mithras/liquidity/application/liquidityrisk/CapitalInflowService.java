package cn.zswltech.mithras.liquidity.application.liquidityrisk;

import cn.zswltech.mithras.dto.liquidityrisk.*;

import java.io.OutputStream;

/**
 * 流动性风险-流入相关操作
 **/
public interface CapitalInflowService {

    //流入明细查询
    AssetInflowDetailRSP inFlowDetail(AssetInflowDetailREQ req);

    //流入明细下载
    void inFlowDetailDownload(AssetInflowDetailREQ req, OutputStream outputStream);

    //短期贷款明细查询
    ShortTermLoanDetailRSP shortTermLoanDetail(ShortTermLoanDetailREQ req);

    //短期贷款明细下载
    void shortTermLoanDownload(ShortTermLoanDetailREQ req, OutputStream outputStream);

    //预计现金流流入图表
    ChartQueryRSP expectedCashFlowInflowChart(ChartQueryREQ req);

    //压力测试流入图表
    ChartQueryRSP pressureTestInflowChart(ChartQueryREQ req);

    void threadRemove();

}
