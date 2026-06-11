package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceCreditInfoREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceCreditInfoRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardFundFinanceController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardFundFinanceCreditInfoExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardFundFinanceCreditInfoModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
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
public class DashboardFundFinanceCreditExportHandle extends ExportHandle<DashboardFundFinanceCreditInfoModel, DashboardFundFinanceCreditInfoExcelExporter> {

    @Resource
    private DashboardFundFinanceController dashboardFundFinanceController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_FUND_FINANCE_CREDIT.name();
    }

    @Override
    public DashboardFundFinanceCreditInfoExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardFundFinanceCreditInfoExcelExporter.class);
    }

    @Override
    public List<DashboardFundFinanceCreditInfoModel> req2ExportList(FileExportREQ req) {
        DashboardFundFinanceCreditInfoREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardFundFinanceCreditInfoREQ.class,"queryDate");
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        String queryDate = req.getExt().get("queryDate");
        dashboardClientOverviewAllREQ.setQueryDate(queryDate);
        R<Map<String, Object>> mapR = dashboardFundFinanceController.listCredit(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardFundFinanceCreditInfoRSP> lists = (List<DashboardFundFinanceCreditInfoRSP>)data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardFundFinanceCreditInfoRSP.class));
        }
        List<DashboardFundFinanceCreditInfoModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardFundFinanceCreditInfoModel model = BeanUtil.copyProperties(rsp, DashboardFundFinanceCreditInfoModel.class, "creditTotalAmount", "creditUsedAmount");
            model.setCreditTotalAmount(Optional.ofNullable(rsp.getCreditTotalAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setCreditUsedAmount(Optional.ofNullable(rsp.getCreditUsedAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null : new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
