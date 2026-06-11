package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStagePaymentDetailREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStagePaymentDetailRSP;
import cn.zswltech.mithras.dto.dashboard.PaymentDetailSumRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardProjectStageController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectStagePaymentExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectStagePaymentModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DashboardProjectStagePaymentExportHandle extends ExportHandle<DashboardProjectStagePaymentModel, DashboardProjectStagePaymentExcelExporter> {

    @Resource
    private DashboardProjectStageController dashboardProjectStageController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_STAGE_PAYMENT.name();
    }

    @Override
    public DashboardProjectStagePaymentExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectStagePaymentExcelExporter.class);
    }

    @Override
    public List<DashboardProjectStagePaymentModel> req2ExportList(FileExportREQ req) {
        DashboardProjectStagePaymentDetailREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectStagePaymentDetailREQ.class);
        R<PaymentDetailSumRSP> sumRSPR = dashboardProjectStageController.paymentList(allREQ);
        PaymentDetailSumRSP data = sumRSPR.getData();
        List<DashboardProjectStagePaymentDetailRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardProjectStagePaymentModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectStagePaymentModel model = BeanUtil.copyProperties(rsp, DashboardProjectStagePaymentModel.class, "lengthOfStay");
            model.setLengthOfStay(Optional.ofNullable(rsp.getLengthOfStay()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLengthOfStay(Optional.ofNullable(rsp.getLengthOfStay()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLesseeNames(rsp.getClientName());
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStagePaymentModel.class));
        }
        return list;
    }

}
