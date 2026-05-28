package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPlanListRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardOperationPayController;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectPlanController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.excel.exporter.dashboard.DashboardOperationPayExcelExporter;
import cn.zswltech.mithras.service.excel.exporter.dashboard.DashboardProjectPlanExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationPayModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectPlanModel;
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

import static cn.zswltech.mithras.service.util.DashboardHelpUtil.*;

@Component
public class DashboardOperationPayExportHandle extends ExportHandle<DashboardOperationPayModel, DashboardOperationPayExcelExporter> {

    @Resource
    private DashboardOperationPayController dashboardOperationPayController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_OPERATION_PAY_PLAN_EXECUTE.name();
    }

    @Override
    public DashboardOperationPayExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardOperationPayExcelExporter.class);
    }

    @Override
    public List<DashboardOperationPayModel> req2ExportList(FileExportREQ req) {
        DashboardOperationPayListREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardOperationPayListREQ.class);
        R<Map<String, Object>> mapR = dashboardOperationPayController.payList(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardOperationPayListRSP> lists = (List<DashboardOperationPayListRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardOperationPayListRSP.class));
            lists.add(BeanUtil.copyProperties(data.get(AVERAGE_DATA), DashboardOperationPayListRSP.class));
        }
        List<DashboardOperationPayModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardOperationPayModel model = BeanUtil.copyProperties(rsp, DashboardOperationPayModel.class, "payPlanAmount", "payAmount", "difference", "finishRate");
            model.setPayPlanAmount(Optional.ofNullable(rsp.getPayPlanAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setPayAmount(Optional.ofNullable(rsp.getPayAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDifference(Optional.ofNullable(rsp.getDifference()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setFinishRate(Optional.ofNullable(rsp.getFinishRate()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
