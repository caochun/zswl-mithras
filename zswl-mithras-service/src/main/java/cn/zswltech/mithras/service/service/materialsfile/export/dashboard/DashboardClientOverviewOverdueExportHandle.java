package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewOverdueREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewOverdueRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardClientOverviewController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardClientOverviewOverdueExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientOverviewOverdueExcelModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import cn.zswltech.mithras.service.util.DashboardExportUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.zswltech.mithras.service.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.service.util.DashboardHelpUtil.SUM_DATE;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardClientOverviewOverdueExportHandle extends ExportHandle<DashboardClientOverviewOverdueExcelModel, DashboardClientOverviewOverdueExcelExporter> {

    @Resource
    private DashboardClientOverviewController dashboardClientOverviewController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_CLIENT_OVERVIEW_OVERDUE.name();
    }

    @Override
    public DashboardClientOverviewOverdueExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardClientOverviewOverdueExcelExporter.class);
    }

    @Override
    public List<DashboardClientOverviewOverdueExcelModel> req2ExportList(FileExportREQ req) {
        DashboardClientOverviewOverdueREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardClientOverviewOverdueREQ.class);
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        dashboardClientOverviewAllREQ.setPage(1);
        dashboardClientOverviewAllREQ.setPageSize(Integer.MAX_VALUE);
        R<Map<String, Object>> mapR = dashboardClientOverviewController.overduePageList(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        Object o = data.get(RECORDS);
        if (ObjectUtil.isEmpty(o)) {
            return ListUtil.empty();
        }
        PageR pageR = BeanUtil.copyProperties(o, PageR.class);
        if (ObjectUtil.isEmpty(pageR)) {
            return ListUtil.empty();
        }
        List<DashboardClientOverviewOverdueRSP> dashboardClientOverviewAllRSPS = BeanUtil.copyToList(pageR.getList(), DashboardClientOverviewOverdueRSP.class);
        if (CollectionUtil.isEmpty(req.getIds())) {
            dashboardClientOverviewAllRSPS.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardClientOverviewOverdueRSP.class));
        }
        List<DashboardClientOverviewOverdueExcelModel> list = new ArrayList<>();
        dashboardClientOverviewAllRSPS.forEach(rsp -> {
            DashboardClientOverviewOverdueExcelModel model = BeanUtil.copyProperties(rsp, DashboardClientOverviewOverdueExcelModel.class);
            model.setPenaltyInterestRate(DashboardExportUtil.spliceHundred(rsp.getPenaltyInterestRate()));
            list.add(model);
        });
        return list;
    }

}
