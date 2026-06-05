package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectInfoRentThisMonthREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectInfoRentThisMonthRSP;
import cn.zswltech.mithras.dto.dashboard.RentThisMonthSumRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectInfoController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectRentThisMonthExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectRentThisMonthModel;
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
public class DashboardProjectRentThisMonthExportHandle extends ExportHandle<DashboardProjectRentThisMonthModel, DashboardProjectRentThisMonthExcelExporter> {

    @Resource
    private DashboardProjectInfoController dashboardProjectInfoController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_RENT_THIS_MONTH.name();
    }

    @Override
    public DashboardProjectRentThisMonthExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectRentThisMonthExcelExporter.class);
    }

    @Override
    public List<DashboardProjectRentThisMonthModel> req2ExportList(FileExportREQ req) {
        DashboardProjectInfoRentThisMonthREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectInfoRentThisMonthREQ.class);
        allREQ.setIds(req.getIds());
        R<RentThisMonthSumRSP> sumRSPR = dashboardProjectInfoController.rentThisMonthList(allREQ);
        RentThisMonthSumRSP data = sumRSPR.getData();
        List<DashboardProjectInfoRentThisMonthRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardProjectRentThisMonthModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectRentThisMonthModel model = BeanUtil.copyProperties(rsp, DashboardProjectRentThisMonthModel.class, "rent", "principalAmount", "interestAmount", "principalBalanceAmount", "creditAmount");
            model.setCreditAmount(DashboardExportUtil.valueUnitDTO2String(rsp.getCreditAmount()));
            model.setRent(DashboardExportUtil.valueUnitDTO2String(rsp.getRent()));
            model.setPrincipalAmount(DashboardExportUtil.valueUnitDTO2String(rsp.getPrincipalAmount()));
            model.setInterestAmount(DashboardExportUtil.valueUnitDTO2String(rsp.getInterestAmount()));
            model.setLesseeNames(rsp.getClientName());
            model.setIsOverdue(ObjectUtil.equals(YesOrNoNumberEnum.YES.getCode(), rsp.getIsOverdue()) ? "是" : "否");
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectRentThisMonthModel.class));
        }
        return list;
    }

}
