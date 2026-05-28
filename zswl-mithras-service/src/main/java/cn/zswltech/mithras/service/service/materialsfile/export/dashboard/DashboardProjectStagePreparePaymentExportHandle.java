package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStagePaymentDetailREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStagePaymentDetailRSP;
import cn.zswltech.mithras.dto.dashboard.PaymentDetailSumRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectStageController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.excel.exporter.dashboard.DashboardProjectStagePaymentExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectStagePaymentModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import cn.zswltech.mithras.service.util.DashboardExportUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class DashboardProjectStagePreparePaymentExportHandle extends ExportHandle<DashboardProjectStagePaymentModel, DashboardProjectStagePaymentExcelExporter> {

    @Resource
    private DashboardProjectStageController dashboardProjectStageController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_STAGE_PREPARE_PAYMENT.name();
    }

    @Override
    public DashboardProjectStagePaymentExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectStagePaymentExcelExporter.class);
    }

    @Override
    public List<DashboardProjectStagePaymentModel> req2ExportList(FileExportREQ req) {
        DashboardProjectStagePaymentDetailREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectStagePaymentDetailREQ.class);
        R<PaymentDetailSumRSP> sumRSPR = dashboardProjectStageController.preparePaymentList(allREQ);
        PaymentDetailSumRSP data = sumRSPR.getData();
        List<DashboardProjectStagePaymentDetailRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }

        List<DashboardProjectStagePaymentModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectStagePaymentModel model = BeanUtil.copyProperties(rsp, DashboardProjectStagePaymentModel.class, "applyPayAmount", "lengthOfStay", "leaseDuration", "interestRate", "applyTime", "contractAmount");
            model.setApplyPayAmount(DashboardExportUtil.valueUnitDTO2String(rsp.getApplyPayAmount()));
            model.setLengthOfStay(DashboardExportUtil.valueUnitDTO2String(rsp.getLengthOfStay()));
            model.setLeaseDuration(DashboardExportUtil.valueUnitDTO2String(rsp.getLeaseDuration()));
            model.setInterestRate(DashboardExportUtil.valueUnitDTO2String(rsp.getInterestRate()));
            model.setLesseeNames(rsp.getClientName());
            model.setContractAmount(DashboardExportUtil.valueUnitDTO2String(rsp.getContractAmount()));
            model.setInterestRate(DashboardExportUtil.spliceHundred(DashboardExportUtil.valueUnitDTO2String(rsp.getInterestRate())));
            model.setApplyTime(Optional.ofNullable(rsp.getApplyTime()).map(e -> e.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))).orElse(null));
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStagePaymentModel.class));
        }
        return list;
    }

}
