package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectInfoOverdueREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectInfoOverdueRSP;
import cn.zswltech.mithras.dto.dashboard.OverDueSumRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectInfoController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectOverdueExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectOverdueModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import cn.zswltech.mithras.dashboard.application.util.DashboardExportUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description 业务工作台-融资视图-还本付息
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardProjectOverdueExportHandle extends ExportHandle<DashboardProjectOverdueModel, DashboardProjectOverdueExcelExporter> {

    @Resource
    private DashboardProjectInfoController dashboardProjectInfoController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_OVERDUE_LIST.name();
    }

    @Override
    public DashboardProjectOverdueExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectOverdueExcelExporter.class);
    }

    @Override
    public List<DashboardProjectOverdueModel> req2ExportList(FileExportREQ req) {
        DashboardProjectInfoOverdueREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectInfoOverdueREQ.class);
        allREQ.setIds(req.getIds());
        R<OverDueSumRSP> sumRSPR = dashboardProjectInfoController.overdueList(allREQ);
        OverDueSumRSP data = sumRSPR.getData();
        List<DashboardProjectInfoOverdueRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardProjectOverdueModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectOverdueModel model = BeanUtil.copyProperties(rsp, DashboardProjectOverdueModel.class, "overdueDuration", "overdueAmount", "interestPenaltyDailyRate", "interestPenaltyAmount", "interestPenaltyReduceAmount");
            model.setOverdueDuration(DashboardExportUtil.valueUnitDTO2String(rsp.getOverdueDuration()));
            model.setOverdueAmount(DashboardExportUtil.valueUnitDTO2String(rsp.getOverdueAmount()));
            model.setInterestPenaltyDailyRate(DashboardExportUtil.valueUnitDTO2String(rsp.getInterestPenaltyDailyRate()));
            model.setInterestPenaltyAmount(DashboardExportUtil.valueUnitDTO2String(rsp.getInterestPenaltyAmount()));
            model.setInterestPenaltyReduceAmount(DashboardExportUtil.valueUnitDTO2String(rsp.getInterestPenaltyReduceAmount()));
            model.setLesseeNames(rsp.getClientName());
            model.setInterestPenaltyDailyRate(DashboardExportUtil.spliceHundred(DashboardExportUtil.valueUnitDTO2String(rsp.getInterestPenaltyDailyRate())));
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectOverdueModel.class));
        }
        return list;
    }

}
