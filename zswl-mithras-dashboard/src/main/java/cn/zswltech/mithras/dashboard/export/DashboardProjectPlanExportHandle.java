package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardProjectPayController;
import cn.zswltech.mithras.dashboard.controller.DashboardProjectPlanController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectPayExcelExporter;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectPlanExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPayModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPlanModel;
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

@Component
public class DashboardProjectPlanExportHandle extends ExportHandle<DashboardProjectPlanModel, DashboardProjectPlanExcelExporter> {

    @Resource
    private DashboardProjectPlanController dashboardProjectPlanController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_VIEW_PLAN_EXECUTE.name();
    }

    @Override
    public DashboardProjectPlanExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectPlanExcelExporter.class);
    }

    @Override
    public List<DashboardProjectPlanModel> req2ExportList(FileExportREQ req) {
        DashboardProjectPlanListREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectPlanListREQ.class);
        R<Map<String, Object>> mapR = dashboardProjectPlanController.planList(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardProjectPlanListRSP> lists = (List<DashboardProjectPlanListRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardProjectPlanListRSP.class));
        }
        List<DashboardProjectPlanModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectPlanModel model = BeanUtil.copyProperties(rsp, DashboardProjectPlanModel.class, "contractAmount", "commission", "earnestMoney",
                    "irr", "actualPayAmount", "contractLimitYear");
            model.setActualPayAmount(Optional.ofNullable(rsp.getActualPayAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setContractAmount(Optional.ofNullable(rsp.getContractAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCommission(Optional.ofNullable(rsp.getCommission()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setEarnestMoney(Optional.ofNullable(rsp.getEarnestMoney()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setIrr(Optional.ofNullable(rsp.getIrr()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setActualPayAmount(Optional.ofNullable(rsp.getActualPayAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setContractLimitYear(Optional.ofNullable(rsp.getContractLimitYear()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
