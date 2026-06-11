package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStageRepaymentDetailREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStageRepaymentDetailRSP;
import cn.zswltech.mithras.dto.dashboard.RePaymentDetailSumRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardProjectStageController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectStageRepaymentExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectStageRepaymentModel;
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
public class DashboardProjectStageRePaymentExportHandle extends ExportHandle<DashboardProjectStageRepaymentModel, DashboardProjectStageRepaymentExcelExporter> {

    @Resource
    private DashboardProjectStageController dashboardProjectStageController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_STAGE_PREPARE_REPAYMENT.name();
    }

    @Override
    public DashboardProjectStageRepaymentExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectStageRepaymentExcelExporter.class);
    }

    @Override
    public List<DashboardProjectStageRepaymentModel> req2ExportList(FileExportREQ req) {
        DashboardProjectStageRepaymentDetailREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectStageRepaymentDetailREQ.class);
        R<RePaymentDetailSumRSP> sumRSPR = dashboardProjectStageController.repaymentList(allREQ);
        RePaymentDetailSumRSP data = sumRSPR.getData();
        List<DashboardProjectStageRepaymentDetailRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardProjectStageRepaymentModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectStageRepaymentModel model = BeanUtil.copyProperties(rsp, DashboardProjectStageRepaymentModel.class, "totalCollectionRent", "totalRentBalance", "stockRiskExposure");
            model.setTotalCollectionRent(Optional.ofNullable(rsp.getTotalCollectionRent()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setTotalRentBalance(Optional.ofNullable(rsp.getTotalRentBalance()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setStockRiskExposure(Optional.ofNullable(rsp.getStockRiskExposure()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStageRepaymentModel.class));
        }
        return list;
    }

}
