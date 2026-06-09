package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.interfaces.DashboardFundFinanceController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardFundFinanceLoanInfoExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardFundFinanceLoanInfoModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.export.ExportHandle;
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
public class DashboardFundFinanceLoanInfoExportHandle extends ExportHandle<DashboardFundFinanceLoanInfoModel, DashboardFundFinanceLoanInfoExcelExporter> {

    @Resource
    private DashboardFundFinanceController dashboardFundFinanceController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_FUND_FINANCE_LOAN_INFO.name();
    }

    @Override
    public DashboardFundFinanceLoanInfoExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardFundFinanceLoanInfoExcelExporter.class);
    }

    @Override
    public List<DashboardFundFinanceLoanInfoModel> req2ExportList(FileExportREQ req) {
        DashboardFundFinanceLoanInfoREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardFundFinanceLoanInfoREQ.class, "queryDate");
        String queryDate = req.getExt().get("queryDate");
        dashboardClientOverviewAllREQ.setQueryDate(queryDate);
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        dashboardClientOverviewAllREQ.setPage(1);
        dashboardClientOverviewAllREQ.setPageSize(Integer.MAX_VALUE);
        R<Map<String, Object>> mapR = dashboardFundFinanceController.listLoanInfo(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        Object o = data.get(RECORDS);
        if (ObjectUtil.isEmpty(o)) {
            return ListUtil.empty();
        }
        PageR pageR = BeanUtil.copyProperties(o, PageR.class);
        if (ObjectUtil.isEmpty(pageR)) {
            return ListUtil.empty();
        }
        List<DashboardFundFinanceLoanInfoRSP> dashboardClientOverviewAllRSPS = BeanUtil.copyToList(pageR.getList(), DashboardFundFinanceLoanInfoRSP.class);
        if (CollectionUtil.isEmpty(req.getIds())) {
            dashboardClientOverviewAllRSPS.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardFundFinanceLoanInfoRSP.class));
        }
        List<DashboardFundFinanceLoanInfoModel> list = new ArrayList<>();
        dashboardClientOverviewAllRSPS.forEach(rsp -> {
            DashboardFundFinanceLoanInfoModel model = BeanUtil.copyProperties(rsp, DashboardFundFinanceLoanInfoModel.class, "relatedContractCodeList");
            if (ObjectUtil.isNotEmpty(rsp.getRelatedContractCodeList())) {
                model.setRelatedContractCodeList(String.join(",", rsp.getRelatedContractCodeList()));
            }
            list.add(model);
        });
        return list;
    }

}
