package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStageEstablishDetailREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStageEstablishDetailRSP;
import cn.zswltech.mithras.dto.dashboard.EstablishDetailSumRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectStageController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.excel.exporter.dashboard.DashboardProjectStageEstablishExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectStageEstablishModel;
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
public class DashboardProjectStageEstablishExportHandle extends ExportHandle<DashboardProjectStageEstablishModel, DashboardProjectStageEstablishExcelExporter> {

    @Resource
    private DashboardProjectStageController dashboardProjectStageController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_STAGE_ESTABLISH.name();
    }

    @Override
    public DashboardProjectStageEstablishExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectStageEstablishExcelExporter.class);
    }

    @Override
    public List<DashboardProjectStageEstablishModel> req2ExportList(FileExportREQ req) {
        DashboardProjectStageEstablishDetailREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectStageEstablishDetailREQ.class);
        R<EstablishDetailSumRSP> sumRSPR = dashboardProjectStageController.establishList(allREQ);
        EstablishDetailSumRSP data = sumRSPR.getData();
        List<DashboardProjectStageEstablishDetailRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardProjectStageEstablishModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectStageEstablishModel model = BeanUtil.copyProperties(rsp, DashboardProjectStageEstablishModel.class, "creditAmount", "approveElapsedTime", "lengthOfStay");
            model.setLengthOfStay(Optional.ofNullable(rsp.getLengthOfStay()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCreditAmount(Optional.ofNullable(rsp.getCreditAmount()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setApproveElapsedTime(Optional.ofNullable(rsp.getApproveElapsedTime()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLesseeNames(rsp.getClientName());
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStageEstablishModel.class));
        }
        return list;
    }

}
