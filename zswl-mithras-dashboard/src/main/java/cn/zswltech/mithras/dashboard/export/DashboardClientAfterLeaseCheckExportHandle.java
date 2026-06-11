package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardClientAfterLeaseController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardClientAfterLeaseCheckExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardClientAfterLeaseCheckModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description 统一工作台-客户视图-客户一览-租后检查
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardClientAfterLeaseCheckExportHandle extends ExportHandle<DashboardClientAfterLeaseCheckModel, DashboardClientAfterLeaseCheckExcelExporter> {

    @Resource
    private DashboardClientAfterLeaseController dashboardClientAfterLeaseController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_CLIENT_OVERVIEW_AFTER_LEASE.name();
    }

    @Override
    public DashboardClientAfterLeaseCheckExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardClientAfterLeaseCheckExcelExporter.class);
    }

    @Override
    public List<DashboardClientAfterLeaseCheckModel> req2ExportList(FileExportREQ req) {
        DashboardClientAfterLeaseCheckREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardClientAfterLeaseCheckREQ.class);
        allREQ.setPage(1);
        allREQ.setPageSize(Integer.MAX_VALUE);
        R<PageR<DashboardClientAfterLeaseCheckRSP>> sumRSPR = dashboardClientAfterLeaseController.afterLeaseCheckList(allREQ);
        PageR<DashboardClientAfterLeaseCheckRSP> data = sumRSPR.getData();
        List<DashboardClientAfterLeaseCheckRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardClientAfterLeaseCheckModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardClientAfterLeaseCheckModel model = BeanUtil.copyProperties(rsp, DashboardClientAfterLeaseCheckModel.class);
            list.add(model);
        });
        return list;
    }

}
