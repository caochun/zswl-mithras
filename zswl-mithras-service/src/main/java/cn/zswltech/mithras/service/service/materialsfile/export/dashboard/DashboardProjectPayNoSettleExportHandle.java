package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPayNoSettleREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPayNoSettleRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectInfoController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectPayNoSettleExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPayNoSettleModel;
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
public class DashboardProjectPayNoSettleExportHandle extends ExportHandle<DashboardProjectPayNoSettleModel, DashboardProjectPayNoSettleExcelExporter> {

    @Resource
    private DashboardProjectInfoController dashboardProjectInfoController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_PROJECT_PAY_NO_SETTLE.name();
    }

    @Override
    public DashboardProjectPayNoSettleExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectPayNoSettleExcelExporter.class);
    }

    @Override
    public List<DashboardProjectPayNoSettleModel> req2ExportList(FileExportREQ req) {
        DashboardProjectPayNoSettleREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectPayNoSettleREQ.class);
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        R<Map<String, Object>> mapR = dashboardProjectInfoController.listProjectPayNoSettle(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardProjectPayNoSettleRSP> lists = (List<DashboardProjectPayNoSettleRSP>) data.get(RECORDS);
        if(ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardProjectPayNoSettleRSP.class));
        }
        List<DashboardProjectPayNoSettleModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectPayNoSettleModel model = BeanUtil.copyProperties(rsp, DashboardProjectPayNoSettleModel.class, "earnestAmount", "stockRiskExposure", "actualPayAmount",
                    "balanceAmount", "principalBalanceAmount", "interestBalanceAmount", "leaseDuration", "remainingLeaseDuration", "irr");
            model.setEarnestAmount(Optional.ofNullable(rsp.getEarnestAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setStockRiskExposure(Optional.ofNullable(rsp.getStockRiskExposure()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setActualPayAmount(Optional.ofNullable(rsp.getActualPayAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setBalanceAmount(Optional.ofNullable(rsp.getBalanceAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setPrincipalBalanceAmount(Optional.ofNullable(rsp.getPrincipalBalanceAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setInterestBalanceAmount(Optional.ofNullable(rsp.getInterestBalanceAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setLeaseDuration(Optional.ofNullable(rsp.getLeaseDuration()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setRemainingLeaseDuration(Optional.ofNullable(rsp.getRemainingLeaseDuration()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setIrr(DashboardExportUtil.spliceHundred(DashboardExportUtil.valueUnitDTO2String(rsp.getIrr())));
            model.setLesseeNames(rsp.getClientName());
            list.add(model);
        });
        return list;
    }

}
