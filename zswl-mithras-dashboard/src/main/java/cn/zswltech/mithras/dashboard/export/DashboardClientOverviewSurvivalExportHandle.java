package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewSurvivalREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewSurvivalRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardClientOverviewController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardClientOverviewAllExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewAllExcelModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.SUM_DATE;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardClientOverviewSurvivalExportHandle extends ExportHandle<DashboardClientOverviewAllExcelModel, DashboardClientOverviewAllExcelExporter> {

    @Resource
    private DashboardClientOverviewController dashboardClientOverviewController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_CLIENT_OVERVIEW_SURVIVAL.name();
    }

    @Override
    public DashboardClientOverviewAllExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardClientOverviewAllExcelExporter.class);
    }

    @Override
    public List<DashboardClientOverviewAllExcelModel> req2ExportList(FileExportREQ req) {
        DashboardClientOverviewSurvivalREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardClientOverviewSurvivalREQ.class);
        dashboardClientOverviewAllREQ.setClientIds(req.getIds());
        dashboardClientOverviewAllREQ.setPage(1);
        dashboardClientOverviewAllREQ.setPageSize(Integer.MAX_VALUE);
        R<Map<String, Object>> mapR = dashboardClientOverviewController.survivalPageList(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        Object o = data.get(RECORDS);
        if (ObjectUtil.isEmpty(o)) {
            return ListUtil.empty();
        }
        PageR pageR = BeanUtil.copyProperties(o, PageR.class);
        if (ObjectUtil.isEmpty(pageR)) {
            return ListUtil.empty();
        }
        List<DashboardClientOverviewSurvivalRSP> dashboardClientOverviewAllRSPS = BeanUtil.copyToList(pageR.getList(), DashboardClientOverviewSurvivalRSP.class);
        if (CollectionUtil.isEmpty(req.getIds())) {
            dashboardClientOverviewAllRSPS.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardClientOverviewSurvivalRSP.class));
        }
        List<DashboardClientOverviewAllExcelModel> list = new ArrayList<>();
        dashboardClientOverviewAllRSPS.forEach(rsp -> {
            DashboardClientOverviewAllExcelModel model = BeanUtil.copyProperties(rsp, DashboardClientOverviewAllExcelModel.class, "creditAmount", "principalBalanceAmount", "stockRiskExposure");
            model.setCreditAmount(Optional.ofNullable(rsp.getCreditAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setPrincipalBalanceAmount(Optional.ofNullable(rsp.getPrincipalBalanceAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setStockRiskExposure(Optional.ofNullable(rsp.getStockRiskExposure()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
