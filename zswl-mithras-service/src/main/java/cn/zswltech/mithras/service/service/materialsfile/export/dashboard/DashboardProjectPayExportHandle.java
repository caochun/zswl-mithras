package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPayListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPayListRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectPayController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectPayExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPayModel;
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

import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.*;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description 业务工作台-融资视图-还本付息
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardProjectPayExportHandle extends ExportHandle<DashboardProjectPayModel, DashboardProjectPayExcelExporter> {

    @Resource
    private DashboardProjectPayController dashboardProjectPayController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_VIEW_INVEST_CASE.name();
    }

    @Override
    public DashboardProjectPayExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectPayExcelExporter.class);
    }

    @Override
    public List<DashboardProjectPayModel> req2ExportList(FileExportREQ req) {
        DashboardProjectPayListREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectPayListREQ.class);
        R<Map<String, Object>> mapR = dashboardProjectPayController.actualPayList(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardProjectPayListRSP> lists = (List<DashboardProjectPayListRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardProjectPayListRSP.class));
            lists.add(BeanUtil.copyProperties(data.get(AVERAGE_DATA), DashboardProjectPayListRSP.class));
        }
        List<DashboardProjectPayModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectPayModel model = BeanUtil.copyProperties(rsp, DashboardProjectPayModel.class, "actualPayAmount", "duration", "actualIrr",
                    "interestRate", "consultingFeeRate", "commissionRate", "earnest");
            model.setActualPayAmount(Optional.ofNullable(rsp.getActualPayAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setDuration(Optional.ofNullable(rsp.getDuration()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setActualIrr(Optional.ofNullable(rsp.getActualIrr()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setInterestRate(Optional.ofNullable(rsp.getInterestRate()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setConsultingFeeRate(Optional.ofNullable(rsp.getConsultingFeeRate()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCommissionRate(Optional.ofNullable(rsp.getCommissionRate()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setEarnest(Optional.ofNullable(rsp.getEarnest()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
