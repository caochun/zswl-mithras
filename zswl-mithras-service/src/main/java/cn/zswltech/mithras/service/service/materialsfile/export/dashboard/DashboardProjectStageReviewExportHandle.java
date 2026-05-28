package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectStageController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.excel.exporter.dashboard.DashboardProjectStageReviewExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectStageReviewModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DashboardProjectStageReviewExportHandle extends ExportHandle<DashboardProjectStageReviewModel, DashboardProjectStageReviewExcelExporter> {

    @Resource
    private DashboardProjectStageController dashboardProjectStageController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_STAGE_REVIEW.name();
    }

    @Override
    public DashboardProjectStageReviewExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectStageReviewExcelExporter.class);
    }

    @Override
    public List<DashboardProjectStageReviewModel> req2ExportList(FileExportREQ req) {
        DashboardProjectStageReviewDetailREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectStageReviewDetailREQ.class);
        R<ReviewDetailSumRSP> sumRSPR = dashboardProjectStageController.reviewList(allREQ);
        ReviewDetailSumRSP data = sumRSPR.getData();
        List<DashboardProjectStageReviewDetailRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStageReviewDetailRSP.class));
        }
        List<DashboardProjectStageReviewModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectStageReviewModel model = BeanUtil.copyProperties(rsp, DashboardProjectStageReviewModel.class, "creditAmount", "approveElapsedTime");
            model.setLengthOfStay(Optional.ofNullable(rsp.getLengthOfStay()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCreditAmount(Optional.ofNullable(rsp.getCreditAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setApproveElapsedTime(Optional.ofNullable(rsp.getApproveElapsedTime()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLesseeNames(rsp.getClientName());
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStageReviewModel.class));
        }
        return list;
    }

}
