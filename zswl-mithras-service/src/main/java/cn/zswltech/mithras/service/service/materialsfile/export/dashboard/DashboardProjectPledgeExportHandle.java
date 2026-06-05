package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPledgeREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectPledgeRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardProjectInfoController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.domain.enums.DashboardPledgeTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingNewStatusEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectPledgeExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectPledgeModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
public class DashboardProjectPledgeExportHandle extends ExportHandle<DashboardProjectPledgeModel, DashboardProjectPledgeExcelExporter> {

    @Resource
    private DashboardProjectInfoController dashboardProjectInfoController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_PLEDGE.name();
    }

    @Override
    public DashboardProjectPledgeExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectPledgeExcelExporter.class);
    }

    @Override
    public List<DashboardProjectPledgeModel> req2ExportList(FileExportREQ req) {
        DashboardProjectPledgeREQ dashboardClientOverviewAllREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectPledgeREQ.class);
        dashboardClientOverviewAllREQ.setIds(req.getIds());
        R<Map<String, Object>> mapR = dashboardProjectInfoController.listPledge(dashboardClientOverviewAllREQ);
        Map<String, Object> data = mapR.getData();
        List<DashboardProjectPledgeRSP> lists = (List<DashboardProjectPledgeRSP>) data.get(RECORDS);
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        if (CollectionUtil.isEmpty(req.getIds())) {
            lists.add(BeanUtil.copyProperties(data.get(SUM_DATE), DashboardProjectPledgeRSP.class));
        }
        List<DashboardProjectPledgeModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectPledgeModel model = BeanUtil.copyProperties(rsp, DashboardProjectPledgeModel.class, "totalPayAmount", "residualRent");
            // 转义
            if (Objects.equals("DK", rsp.getType())) {
                FundFinancingBizTypeEnum type = FundFinancingBizTypeEnum.finaByName(rsp.getBusinessType());
                model.setBusinessType(Optional.ofNullable(type).map(FundFinancingBizTypeEnum::getDisplay).orElse(null));
            }
            if (Objects.equals("ZR", rsp.getType())) {
                DirectFinancingType type = DirectFinancingType.findByName(rsp.getBusinessType());
                model.setBusinessType(Optional.ofNullable(type).map(DirectFinancingType::display).orElse(null));
            }
            DashboardPledgeTypeEnum dashboardPledgeTypeEnum = DashboardPledgeTypeEnum.findByName(rsp.getPledgeStatus());
            FundFinancingNewStatusEnum fundFinancingNewStatusEnum = FundFinancingNewStatusEnum.finaByName(rsp.getFinancingStatus());
            model.setFinancingStatus(Optional.ofNullable(fundFinancingNewStatusEnum).map(FundFinancingNewStatusEnum::getDisplay).orElse(null));
            model.setPledgeStatus(Optional.ofNullable(dashboardPledgeTypeEnum).map(DashboardPledgeTypeEnum::getDisplay).orElse(null));
            model.setTotalPayAmount(Optional.ofNullable(rsp.getTotalPayAmount()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setResidualRent(Optional.ofNullable(rsp.getResidualRent()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            model.setRemainingPrincipal(Optional.ofNullable(rsp.getRemainingPrincipal()).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            list.add(model);
        });
        return list;
    }

}
