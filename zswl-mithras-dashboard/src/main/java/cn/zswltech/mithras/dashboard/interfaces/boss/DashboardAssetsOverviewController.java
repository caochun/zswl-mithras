package cn.zswltech.mithras.dashboard.interfaces.boss;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.DashboardAssetsOverviewApi;
import cn.zswltech.mithras.dto.dashboard.boss.BalanceOverviewRSP;
import cn.zswltech.mithras.dto.dashboard.boss.AssetsOverviewDetailListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.LoanOverviewRSP;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.dashboard.application.boss.DashboardAssetsOverviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author yangxiong
 * @date 2024/5/15/19:23
 * @description
 */
@Slf4j
@RestController
public class DashboardAssetsOverviewController implements DashboardAssetsOverviewApi {
    @Resource
    private DashboardAssetsOverviewService dashboardAssetsOverviewService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<BalanceOverviewRSP> getBalanceOverview() {
        return R.ok(dashboardAssetsOverviewService.getBalanceOverview());
    }

    @Override
    public R<LoanOverviewRSP> getLoanOverview() {
        return R.ok(dashboardAssetsOverviewService.getLoanOverview());
    }

    @Override
    public R<List<AssetsOverviewDetailListRSP>> getDetailByProvince() {
        return R.ok(dashboardAssetsOverviewService.listByProvince());
    }

    @Override
    public void exportDetailByProvince() {
        try {
            List<AssetsOverviewDetailListRSP> dataList = Optional.ofNullable(getDetailByProvince()).map(R::getData).orElse(Collections.emptyList());
            if (CollectionUtil.isEmpty(dataList)) {
                throw new MithrasException("暂无可导出数据");
            }
            ExcelWriter excelWriter = ExcelUtil.getWriter(true);
            excelWriter.getSheet().setDefaultColumnWidth(20);
            excelWriter.writeHeadRow(ListUtil.of("序号", "省份", "资产余额（万元）", "资产占比", "本年投放金额（万元）", "存量项目数"));
            for (AssetsOverviewDetailListRSP rsp : dataList) {
                excelWriter.writeRow(ListUtil.of(
                        rsp.getRank(),
                        rsp.getDimensionality(),
                        Optional.ofNullable(rsp.getAssetsBalance()).map(e -> new BigDecimal(e.getValue()).setScale(2, RoundingMode.HALF_UP)).orElse(BigDecimal.ZERO),
                        Optional.ofNullable(rsp.getAssetsProportion()).map(e -> new BigDecimal(e.getValue()).setScale(2, RoundingMode.HALF_UP)).orElse(BigDecimal.ZERO),
                        Optional.ofNullable(rsp.getLoanThisYear()).map(e -> new BigDecimal(e.getValue()).setScale(2, RoundingMode.HALF_UP)).orElse(BigDecimal.ZERO),
                        rsp.getStockProjectQuantity()
                ));
            }
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("省份资产排名.xlsx", StandardCharsets.UTF_8.name()));
            excelWriter.flush(httpServletResponse.getOutputStream(), true);
        } catch (Exception e) {
            log.error("导出发生未知异常", e);
            throw new MithrasException("导出发生未知异常");
        }
    }

    @Override
    public R<List<AssetsOverviewDetailListRSP>> getDetailByArea() {
        return R.ok(dashboardAssetsOverviewService.listByArea());
    }
}
