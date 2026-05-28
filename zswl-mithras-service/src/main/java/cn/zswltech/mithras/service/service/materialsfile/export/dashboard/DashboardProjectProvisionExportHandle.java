package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectProvisionREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectProvisionRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectInfoController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.excel.exporter.dashboard.DashboardProjectProvisionExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardProjectProvisionModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import cn.zswltech.mithras.service.util.DashboardExportUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.zswltech.mithras.service.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.service.util.DashboardHelpUtil.SUM_DATE;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description 业务工作台-融资视图-还本付息
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardProjectProvisionExportHandle extends ExportHandle<DashboardProjectProvisionModel, DashboardProjectProvisionExcelExporter> {

    @Resource
    private DashboardProjectInfoController dashboardProjectInfoController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_PROVISION.name();
    }

    @Override
    public DashboardProjectProvisionExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectProvisionExcelExporter.class);
    }

    @Override
    public List<DashboardProjectProvisionModel> req2ExportList(FileExportREQ req) {
        DashboardProjectProvisionREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectProvisionREQ.class);
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        R<Map<String, Object>> mapR = dashboardProjectInfoController.listProvision(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardProjectProvisionRSP> lists = (List<DashboardProjectProvisionRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardProjectProvisionRSP.class));
        }
        List<DashboardProjectProvisionModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectProvisionModel model = BeanUtil.copyProperties(rsp, DashboardProjectProvisionModel.class, "contractAmount", "principalBalance", "earnest",
                    "stockRiskExposure", "provision", "provisionRate");
            model.setContractAmount(Optional.ofNullable(rsp.getContractAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setPrincipalBalance(Optional.ofNullable(rsp.getPrincipalBalance()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setEarnest(Optional.ofNullable(rsp.getEarnest()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setStockRiskExposure(Optional.ofNullable(rsp.getStockRiskExposure()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProvision(Optional.ofNullable(rsp.getProvision()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProvisionRate(Optional.ofNullable(rsp.getProvisionRate()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setProvisionRate(DashboardExportUtil.spliceHundred(DashboardExportUtil.valueUnitDTO2String(rsp.getProvisionRate())));
            list.add(model);
        });
        return list;
    }

}
