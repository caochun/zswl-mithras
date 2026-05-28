package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStageReviewDetailREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStageReviewDetailRSP;
import cn.zswltech.mithras.dto.dashboard.ReviewDetailSumRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectStageController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.excel.exporter.dashboard.DashboardProjectStageReviewLegalExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectStageReviewLegalModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class DashboardProjectStageReviewLegalReportExportHandle extends ExportHandle<DashboardProjectStageReviewLegalModel, DashboardProjectStageReviewLegalExcelExporter> {

    @Resource
    private DashboardProjectStageController dashboardProjectStageController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_STAGE_REVIEW_NO_LEGAL_REPORT.name();
    }

    @Override
    public DashboardProjectStageReviewLegalExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectStageReviewLegalExcelExporter.class);
    }

    @Override
    public List<DashboardProjectStageReviewLegalModel> req2ExportList(FileExportREQ req) {
        DashboardProjectStageReviewDetailREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectStageReviewDetailREQ.class);
        R<ReviewDetailSumRSP> sumRSPR = dashboardProjectStageController.reviewLegalReportList(allREQ);
        ReviewDetailSumRSP data = sumRSPR.getData();
        List<DashboardProjectStageReviewDetailRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardProjectStageReviewLegalModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectStageReviewLegalModel model = BeanUtil.copyProperties(rsp, DashboardProjectStageReviewLegalModel.class, "creditAmount", "approveElapsedTime");
            model.setCreditAmount(Optional.ofNullable(rsp.getCreditAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLengthOfStay(Optional.ofNullable(rsp.getLengthOfStay()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLesseeNames(rsp.getClientName());
            model.setDueDiligenceReportUploadTime(ObjectUtil.isEmpty(rsp.getDueDiligenceReportUploadTime()) ? null : rsp.getDueDiligenceReportUploadTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            model.setApplyTime(ObjectUtil.isEmpty(rsp.getApplyTime()) ? null : rsp.getApplyTime().format(DateTimeFormatter.ofPattern(DatePattern.ISO8601_PATTERN)));
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStageReviewLegalModel.class));
        }
        return list;
    }

}
