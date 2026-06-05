package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceBalanceREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceBalanceRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardFundFinanceController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardFundFinanceBalanceExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardFundFinanceBalanceModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import cn.zswltech.mithras.dashboard.application.util.DashboardExportUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.SUM_DATE;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description 业务工作台-融资视图-还本付息
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardFundFinanceBalanceExportHandle extends ExportHandle<DashboardFundFinanceBalanceModel, DashboardFundFinanceBalanceExcelExporter> {

    @Resource
    private DashboardFundFinanceController dashboardFundFinanceController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_FUND_FINANCE_BALANCE.name();
    }

    @Override
    public DashboardFundFinanceBalanceExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardFundFinanceBalanceExcelExporter.class);
    }

    @Override
    public List<DashboardFundFinanceBalanceModel> req2ExportList(FileExportREQ req) {
        DashboardFundFinanceBalanceREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardFundFinanceBalanceREQ.class,"queryDate");
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        String queryDate = req.getExt().get("queryDate");
        dashboardClientOverviewAllREQ.setQueryDate(queryDate);
        dashboardClientOverviewAllREQ.setPage(1);
        dashboardClientOverviewAllREQ.setPageSize(Integer.MAX_VALUE);
        R<Map<String, Object>> mapR = dashboardFundFinanceController.listBalance(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        Object o = data.get(RECORDS);
        if (ObjectUtil.isEmpty(o)) {
            return ListUtil.empty();
        }
        PageR pageR = BeanUtil.copyProperties(o, PageR.class);
        if (ObjectUtil.isEmpty(pageR)) {
            return ListUtil.empty();
        }
        List<DashboardFundFinanceBalanceRSP> dashboardClientOverviewAllRSPS = BeanUtil.copyToList(pageR.getList(), DashboardFundFinanceBalanceRSP.class);
        if (CollectionUtil.isEmpty(req.getIds())) {
            dashboardClientOverviewAllRSPS.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardFundFinanceBalanceRSP.class));
        }
        List<DashboardFundFinanceBalanceModel> list = new ArrayList<>();
        dashboardClientOverviewAllRSPS.forEach(rsp -> {
            DashboardFundFinanceBalanceModel model = BeanUtil.copyProperties(rsp, DashboardFundFinanceBalanceModel.class,
                    "relatedContractCodeList");
            model.setComprehensiveInterestRate(DashboardExportUtil.spliceHundred(rsp.getComprehensiveInterestRate()));
            model.setInterestRate(DashboardExportUtil.spliceHundred(rsp.getInterestRate()));
            if(ObjectUtil.isNotEmpty(rsp.getRelatedContractCodeList())){
                model.setRelatedContractCodeList(String.join(",", rsp.getRelatedContractCodeList()));
            }
            list.add(model);
        });
        return list;
    }

}
