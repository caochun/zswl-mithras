package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceFundsREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceFundsRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.interfaces.DashboardFundFinanceController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardFundFinanceFundsExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardFundFinanceFundsModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.export.ExportHandle;
import cn.zswltech.mithras.dashboard.application.util.DashboardExportUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
public class DashboardFundFinanceCostFundsExportHandle extends ExportHandle<DashboardFundFinanceFundsModel, DashboardFundFinanceFundsExcelExporter> {

    @Resource
    private DashboardFundFinanceController dashboardFundFinanceController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_FUND_FINANCE_COST_FUNDS.name();
    }

    @Override
    public DashboardFundFinanceFundsExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardFundFinanceFundsExcelExporter.class);
    }

    @Override
    public List<DashboardFundFinanceFundsModel> req2ExportList(FileExportREQ req) {
        DashboardFundFinanceFundsREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardFundFinanceFundsREQ.class, "queryDate");
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        String queryDate = req.getExt().get("queryDate");
        dashboardClientOverviewAllREQ.setQueryDate(queryDate);
        dashboardClientOverviewAllREQ.setPage(1);
        dashboardClientOverviewAllREQ.setPageSize(Integer.MAX_VALUE);
        R<Map<String, Object>> mapR = dashboardFundFinanceController.costFunds(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        Object o = data.get(RECORDS);
        if (ObjectUtil.isEmpty(o)) {
            return ListUtil.empty();
        }
        PageR pageR = BeanUtil.copyProperties(o, PageR.class);
        if (ObjectUtil.isEmpty(pageR)) {
            return ListUtil.empty();
        }
        List<DashboardFundFinanceFundsRSP> dashboardClientOverviewAllRSPS = BeanUtil.copyToList(pageR.getList(), DashboardFundFinanceFundsRSP.class);
        if (CollectionUtil.isEmpty(req.getIds())) {
            dashboardClientOverviewAllRSPS.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardFundFinanceFundsRSP.class));
        }
        List<DashboardFundFinanceFundsModel> list = new ArrayList<>();
        dashboardClientOverviewAllRSPS.forEach(rsp -> {
            DashboardFundFinanceFundsModel model = BeanUtil.copyProperties(rsp, DashboardFundFinanceFundsModel.class,
                    "loanAmount", "remainingPrincipleAmount", "relatedContractCodeList");
            model.setLoanAmount(Optional.ofNullable(rsp.getLoanAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setRemainingPrincipleAmount(Optional.ofNullable(rsp.getRemainingPrincipleAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
//            model.setComprehensiveInterestRate(DashboardExportUtil.spliceHundred(rsp.getComprehensiveInterestRate()));
            model.setComprehensiveInterestRate(Optional.ofNullable(rsp.getComprehensiveInterestRate()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            if(ObjectUtil.isNotEmpty(rsp.getRelatedContractCodeList())){
                model.setRelatedContractCodeList(String.join(",", rsp.getRelatedContractCodeList()));
            }
            list.add(model);
        });
        return list;
    }

}
