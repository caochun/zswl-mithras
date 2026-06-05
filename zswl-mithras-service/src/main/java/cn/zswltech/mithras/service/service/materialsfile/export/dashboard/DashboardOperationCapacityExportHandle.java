package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationCapacityListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationCapacityListRSP;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListREQ;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardOperationCapacityController;
import cn.zswltech.mithras.service.controller.dashboard.DashboardOperationPayController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardOperationCapacityExcelExporter;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardOperationPayExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationCapacityModel;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationPayModel;
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
public class DashboardOperationCapacityExportHandle extends ExportHandle<DashboardOperationCapacityModel, DashboardOperationCapacityExcelExporter> {

    @Resource
    private DashboardOperationCapacityController dashboardOperationCapacityController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_OPERATION_CAPACITY_EXECUTE.name();
    }

    @Override
    public DashboardOperationCapacityExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardOperationCapacityExcelExporter.class);
    }

    @Override
    public List<DashboardOperationCapacityModel> req2ExportList(FileExportREQ req) {
        DashboardOperationCapacityListREQ dashboardOperationCapacityListREQ = BeanUtil.copyProperties(req.getExt(), DashboardOperationCapacityListREQ.class);
        R<Map<String, Object>> mapR = dashboardOperationCapacityController.list(dashboardOperationCapacityListREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardOperationCapacityListRSP> lists = (List<DashboardOperationCapacityListRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardOperationCapacityListRSP.class));
            lists.add(BeanUtil.copyProperties(data.get(AVERAGE_DATA), DashboardOperationCapacityListRSP.class));
        }
        List<DashboardOperationCapacityModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardOperationCapacityModel model = BeanUtil.copyProperties(rsp, DashboardOperationCapacityModel.class, "amountSum", "personAverageAmountSum", "pieceAverageAmountSum","adjustPersonSum","projSum","personAverageProjSum");
            model.setAmountSum(Optional.ofNullable(rsp.getAmountSum()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setPersonAverageAmountSum(Optional.ofNullable(rsp.getPersonAverageAmountSum()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setPieceAverageAmountSum(Optional.ofNullable(rsp.getPieceAverageAmountSum()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setAdjustPersonSum(Optional.ofNullable(rsp.getAdjustPersonSum()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProjSum(Optional.ofNullable(rsp.getProjSum()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setPersonAverageProjSum(Optional.ofNullable(rsp.getPersonAverageProjSum()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
