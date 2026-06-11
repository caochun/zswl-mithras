package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceRepayREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceRepayRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardFundFinanceController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardFundFinanceRepayExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardFundFinanceRepayModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
public class DashboardFundFinanceRepayExportHandle extends ExportHandle<DashboardFundFinanceRepayModel, DashboardFundFinanceRepayExcelExporter> {

    @Resource
    private DashboardFundFinanceController dashboardFundFinanceController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_FUND_FINANCE_REPAY.name();
    }

    @Override
    public DashboardFundFinanceRepayExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardFundFinanceRepayExcelExporter.class);
    }

    @Override
    public List<DashboardFundFinanceRepayModel> req2ExportList(FileExportREQ req) {
        DashboardFundFinanceRepayREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardFundFinanceRepayREQ.class,"queryDate");
        // 特殊处理状态数组
        if (Objects.nonNull(req.getExt())) {
            String array = req.getExt().get("writeOffStateList");
            String queryDate = req.getExt().get("queryDate");
            dashboardClientOverviewAllREQ.setQueryDate(queryDate);
            if (StrUtil.isNotBlank(array)) {
                dashboardClientOverviewAllREQ.setWriteOffStateList(JSONUtil.toList(array, String.class));
            }
        }
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        R<Map<String, Object>> mapR = dashboardFundFinanceController.listRepay(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardFundFinanceRepayRSP> lists = (List<DashboardFundFinanceRepayRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardFundFinanceRepayRSP.class));
        }
        List<DashboardFundFinanceRepayModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            List<DashboardFundFinanceRepayRSP.SubListInfo> subListInfo = rsp.getSubListInfo();
            DashboardFundFinanceRepayModel model = BeanUtil.copyProperties(rsp, DashboardFundFinanceRepayModel.class, "loanAmount", "loanBalanceAmount", "repayTotalAmount",
                    "repayPrincipalAmount", "repayInterestAmount", "rentPlanCollectionAmount", "actualRepayAmount", "repayBalanceAmount");
            model.setLoanAmount(Optional.ofNullable(rsp.getLoanAmount()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLoanBalanceAmount(Optional.ofNullable(rsp.getLoanBalanceAmount()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setRepayTotalAmount(Optional.ofNullable(rsp.getRepayTotalAmount()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setRepayPrincipalAmount(Optional.ofNullable(rsp.getRepayPrincipalAmount()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setRepayInterestAmount(Optional.ofNullable(rsp.getRepayInterestAmount()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setActualRepayAmount(Optional.ofNullable(rsp.getActualRepayAmount()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setRepayBalanceAmount(Optional.ofNullable(rsp.getRepayBalanceAmount()).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
