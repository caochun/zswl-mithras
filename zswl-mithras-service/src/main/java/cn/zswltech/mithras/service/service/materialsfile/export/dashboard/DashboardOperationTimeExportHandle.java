package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanListRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationTimeListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationTimeStatisticsRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardOperationTimeController;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectPlanController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardOperationTimeExcelExporter;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectPlanExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationPayModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationTimeModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPlanModel;
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

import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.*;

@Component
public class DashboardOperationTimeExportHandle extends ExportHandle<DashboardOperationTimeModel, DashboardOperationTimeExcelExporter> {

    @Resource
    private DashboardOperationTimeController dashboardOperationTimeController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_OPERATION_TIME_EXECUTE.name();
    }

    @Override
    public DashboardOperationTimeExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardOperationTimeExcelExporter.class);
    }

    @Override
    public List<DashboardOperationTimeModel> req2ExportList(FileExportREQ req) {
        DashboardOperationTimeListREQ dashboardOperationTimeListREQ = BeanUtil.copyProperties(req.getExt(), DashboardOperationTimeListREQ.class);
        R<Map<String, Object>> mapR = dashboardOperationTimeController.detailList(dashboardOperationTimeListREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardOperationTimeStatisticsRSP> lists = (List<DashboardOperationTimeStatisticsRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardOperationTimeStatisticsRSP.class));
            lists.add(BeanUtil.copyProperties(data.get(AVERAGE_DATA), DashboardOperationTimeStatisticsRSP.class));
        }
        List<DashboardOperationTimeModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardOperationTimeModel model = BeanUtil.copyProperties(rsp, DashboardOperationTimeModel.class);
            model.setProjEstaTotalTime(Optional.ofNullable(rsp.getProjEstaTotalTime()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDueDiligenceTotalTime(Optional.ofNullable(rsp.getDueDiligenceTotalTime()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setReviewTotalTime(Optional.ofNullable(rsp.getReviewTotalTime()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setSummaryTotalTime(Optional.ofNullable(rsp.getSummaryTotalTime()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProjEstaPaidInTotalTime(Optional.ofNullable(rsp.getProjEstaPaidInTotalTime()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setReviewPaidInTotalTime(Optional.ofNullable(rsp.getReviewPaidInTotalTime()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
