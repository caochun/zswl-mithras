package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewSettleInThreeMonthREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewSettleInThreeMonthRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardClientOverviewController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardClientOverviewSettleInThreeMonthExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewSettleThreeMonthExcelModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.zswltech.mithras.service.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.service.util.DashboardHelpUtil.SUM_DATE;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardClientOverviewSettleInThreeMonthExportHandle extends ExportHandle<DashboardClientOverviewSettleThreeMonthExcelModel, DashboardClientOverviewSettleInThreeMonthExcelExporter> {

    @Resource
    private DashboardClientOverviewController dashboardClientOverviewController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_CLIENT_OVERVIEW_SETTLE_IN_THREE_MONTH.name();
    }

    @Override
    public DashboardClientOverviewSettleInThreeMonthExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardClientOverviewSettleInThreeMonthExcelExporter.class);
    }

    @Override
    public List<DashboardClientOverviewSettleThreeMonthExcelModel> req2ExportList(FileExportREQ req) {
        DashboardClientOverviewSettleInThreeMonthREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardClientOverviewSettleInThreeMonthREQ.class);
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        dashboardClientOverviewAllREQ.setPage(1);
        dashboardClientOverviewAllREQ.setPageSize(Integer.MAX_VALUE);
        R<Map<String, Object>> mapR = dashboardClientOverviewController.settleInThreeMonthPageList(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        Object o = data.get(RECORDS);
        if (ObjectUtil.isEmpty(o)) {
            return ListUtil.empty();
        }
        PageR pageR = BeanUtil.copyProperties(o, PageR.class);
        if (ObjectUtil.isEmpty(pageR)) {
            return ListUtil.empty();
        }
        List<DashboardClientOverviewSettleInThreeMonthRSP> dashboardClientOverviewAllRSPS = BeanUtil.copyToList(pageR.getList(), DashboardClientOverviewSettleInThreeMonthRSP.class);
        if (CollectionUtil.isEmpty(req.getIds())) {
            dashboardClientOverviewAllRSPS.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardClientOverviewSettleInThreeMonthRSP.class));
        }
        List<DashboardClientOverviewSettleThreeMonthExcelModel> list = new ArrayList<>();
        dashboardClientOverviewAllRSPS.forEach(rsp -> {
            DashboardClientOverviewSettleThreeMonthExcelModel model = BeanUtil.copyProperties(rsp, DashboardClientOverviewSettleThreeMonthExcelModel.class,  "stockRiskExposure", "collectionAmount", "collectionPrincipalAmount", "collectionInterestAmount", "principalBalanceAmount", "interestInterestAmount");
            model.setStockRiskExposure(Optional.ofNullable(rsp.getStockRiskExposure()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCollectionAmount(Optional.ofNullable(rsp.getCollectionAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCollectionPrincipalAmount(Optional.ofNullable(rsp.getCollectionPrincipalAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCollectionInterestAmount(Optional.ofNullable(rsp.getCollectionInterestAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setPrincipalBalanceAmount(Optional.ofNullable(rsp.getPrincipalBalanceAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setInterestInterestAmount(Optional.ofNullable(rsp.getInterestInterestAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
