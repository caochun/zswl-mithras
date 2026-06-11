package cn.zswltech.mithras.dashboard.export;

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
import cn.zswltech.mithras.dashboard.controller.DashboardProjectStageController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectStageReviewNoContractExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectStageReviewNoContractModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class DashboardProjectStageReviewFinishNoContractExportHandle extends ExportHandle<DashboardProjectStageReviewNoContractModel, DashboardProjectStageReviewNoContractExcelExporter> {

    @Resource
    private DashboardProjectStageController dashboardProjectStageController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_STAGE_REVIEW_NO_CONTRACT.name();
    }

    @Override
    public DashboardProjectStageReviewNoContractExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectStageReviewNoContractExcelExporter.class);
    }

    @Override
    public List<DashboardProjectStageReviewNoContractModel> req2ExportList(FileExportREQ req) {
        DashboardProjectStageReviewDetailREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectStageReviewDetailREQ.class);
        R<ReviewDetailSumRSP> sumRSPR = dashboardProjectStageController.reviewFinishNoContractList(allREQ);
        ReviewDetailSumRSP data = sumRSPR.getData();
        List<DashboardProjectStageReviewDetailRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardProjectStageReviewNoContractModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectStageReviewNoContractModel model = BeanUtil.copyProperties(rsp, DashboardProjectStageReviewNoContractModel.class, "creditAmount", "approveElapsedTime");
            model.setLengthOfStay(Optional.ofNullable(rsp.getLengthOfStay()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCreditAmount(Optional.ofNullable(rsp.getCreditAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLesseeNames(rsp.getClientName());
            model.setApplyTime(ObjectUtil.isEmpty(rsp.getApplyTime()) ? null : rsp.getApplyTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStageReviewNoContractModel.class));
        }
        return list;
    }

}
