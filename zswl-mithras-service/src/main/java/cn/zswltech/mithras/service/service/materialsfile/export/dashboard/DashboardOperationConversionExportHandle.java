package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationConversionListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationConversionListRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationTimeListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationTimeStatisticsRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardOperationConversionController;
import cn.zswltech.mithras.service.controller.dashboard.DashboardOperationTimeController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardOperationConversionExcelExporter;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardOperationTimeExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationConversionModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationTimeModel;
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
public class DashboardOperationConversionExportHandle extends ExportHandle<DashboardOperationConversionModel, DashboardOperationConversionExcelExporter> {

    @Resource
    private DashboardOperationConversionController dashboardOperationConversionController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_OPERATION_CONVERSION_EXECUTE.name();
    }

    @Override
    public DashboardOperationConversionExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardOperationConversionExcelExporter.class);
    }

    @Override
    public List<DashboardOperationConversionModel> req2ExportList(FileExportREQ req) {
        DashboardOperationConversionListREQ dashboardOperationConversionListREQ = BeanUtil.copyProperties(req.getExt(), DashboardOperationConversionListREQ.class);
        R<Map<String, Object>> mapR = dashboardOperationConversionController.detailList(dashboardOperationConversionListREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardOperationConversionListRSP> lists = (List<DashboardOperationConversionListRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardOperationConversionListRSP.class));
            lists.add(BeanUtil.copyProperties(data.get(AVERAGE_DATA), DashboardOperationConversionListRSP.class));
        }
        List<DashboardOperationConversionModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardOperationConversionModel model = BeanUtil.copyProperties(rsp, DashboardOperationConversionModel.class);
            model.setAdjustCount(Optional.ofNullable(rsp.getAdjustCount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setVisitCount(Optional.ofNullable(rsp.getVisitCount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProjEstaCount(Optional.ofNullable(rsp.getProjEstaCount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDueDiligenceCount(Optional.ofNullable(rsp.getDueDiligenceCount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setReviewCount(Optional.ofNullable(rsp.getReviewCount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryCount(Optional.ofNullable(rsp.getDeliveryCount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProjEstaVisitCurrentTerm(Optional.ofNullable(rsp.getProjEstaVisitCurrentTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProjEstaVisitLastTerm(Optional.ofNullable(rsp.getProjEstaVisitLastTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProjEstaVisitCurrentAverage(Optional.ofNullable(rsp.getProjEstaVisitCurrentAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProjEstaVisitLastAverage(Optional.ofNullable(rsp.getProjEstaVisitLastAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryVisitCurrentTerm(Optional.ofNullable(rsp.getDeliveryVisitCurrentTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryVisitLastTerm(Optional.ofNullable(rsp.getDeliveryVisitLastTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryVisitLastAverage(Optional.ofNullable(rsp.getDeliveryVisitLastAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryVisitCurrentAverage(Optional.ofNullable(rsp.getDeliveryVisitCurrentAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDueDiliProjEstaCurrentTerm(Optional.ofNullable(rsp.getDueDiliProjEstaCurrentTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDueDiliProjEstaLastTerm(Optional.ofNullable(rsp.getDueDiliProjEstaLastTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDueDiliProjEstaCurrentAverage(Optional.ofNullable(rsp.getDueDiliProjEstaCurrentAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDueDiliProjEstaLastAverage(Optional.ofNullable(rsp.getDueDiliProjEstaLastAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setReviewDueDiliCurrentTerm(Optional.ofNullable(rsp.getReviewDueDiliCurrentTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setReviewDueDiliLastTerm(Optional.ofNullable(rsp.getReviewDueDiliLastTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setReviewDueDiliCurrentAverage(Optional.ofNullable(rsp.getReviewDueDiliCurrentAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setReviewDueDiliLastAverage(Optional.ofNullable(rsp.getReviewDueDiliLastAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryDueDiliCurrentTerm(Optional.ofNullable(rsp.getDeliveryDueDiliCurrentTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryDueDiliLastTerm(Optional.ofNullable(rsp.getDeliveryDueDiliLastTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryDueDiliLastAverage(Optional.ofNullable(rsp.getDeliveryDueDiliLastAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryDueDiliCurrentAverage(Optional.ofNullable(rsp.getDeliveryDueDiliCurrentAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryReviewCurrentTerm(Optional.ofNullable(rsp.getDeliveryReviewCurrentTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryReviewLastTerm(Optional.ofNullable(rsp.getDeliveryReviewLastTerm()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryReviewLastAverage(Optional.ofNullable(rsp.getDeliveryReviewLastAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDeliveryReviewCurrentAverage(Optional.ofNullable(rsp.getDeliveryReviewCurrentAverage()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
