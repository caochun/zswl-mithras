package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectInfoSettleInThreeMonthREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectInfoSettleInThreeMonthRSP;
import cn.zswltech.mithras.dto.dashboard.SettleInThreeMonSumRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.interfaces.DashboardProjectInfoController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectSettleInThreeMonthExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectSettleInThreeMonthModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description 业务工作台-融资视图-还本付息
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardProjectSettleInThreeMonthExportHandle extends ExportHandle<DashboardProjectSettleInThreeMonthModel, DashboardProjectSettleInThreeMonthExcelExporter> {

    @Resource
    private DashboardProjectInfoController dashboardProjectInfoController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_SETTLE_IN_THREE_MONTH.name();
    }

    @Override
    public DashboardProjectSettleInThreeMonthExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectSettleInThreeMonthExcelExporter.class);
    }

    @Override
    public List<DashboardProjectSettleInThreeMonthModel> req2ExportList(FileExportREQ req) {
        DashboardProjectInfoSettleInThreeMonthREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectInfoSettleInThreeMonthREQ.class);
        allREQ.setIds(req.getIds());
        R<SettleInThreeMonSumRSP> sumRSPR = dashboardProjectInfoController.settleInThreeMonthList(allREQ);
        SettleInThreeMonSumRSP data = sumRSPR.getData();
        List<DashboardProjectInfoSettleInThreeMonthRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardProjectSettleInThreeMonthModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectSettleInThreeMonthModel model = BeanUtil.copyProperties(rsp, DashboardProjectSettleInThreeMonthModel.class, "remainingDuration", "collectionAmount", "remainingAmount", "earnestBalanceAmount",
                    "collectionPrincipalAmount", "collectionInterestAmount", "principalBalanceAmount", "interestBalanceAmount");
            model.setRemainingDuration(valueUnitDTO2String(rsp.getRemainingDuration()));
            model.setCollectionAmount(valueUnitDTO2String(rsp.getCollectionAmount()));
            model.setRemainingAmount(valueUnitDTO2String(rsp.getRemainingAmount()));
            model.setEarnestBalanceAmount(valueUnitDTO2String(rsp.getEarnestBalanceAmount()));
            model.setCollectionPrincipalAmount(valueUnitDTO2String(rsp.getCollectionPrincipalAmount()));
            model.setCollectionInterestAmount(valueUnitDTO2String(rsp.getCollectionInterestAmount()));
            model.setPrincipalBalanceAmount(valueUnitDTO2String(rsp.getPrincipalBalanceAmount()));
            model.setInterestBalanceAmount(valueUnitDTO2String(rsp.getInterestBalanceAmount()));
            model.setLesseeNames(rsp.getClientName());
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectSettleInThreeMonthModel.class));
        }
        return list;
    }

    private String valueUnitDTO2String(ValueUnitDTO valueUnit){
        return Optional.ofNullable(valueUnit).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null);
    }

}
