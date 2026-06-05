package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.ContractDetailSumRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStageContractDetailREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardProjectStageContractDetailRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.interfaces.DashboardProjectStageController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardProjectStageContractExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardProjectStageContractModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import cn.zswltech.mithras.dashboard.application.util.DashboardExportUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DashboardProjectStageContractExportHandle extends ExportHandle<DashboardProjectStageContractModel, DashboardProjectStageContractExcelExporter> {

    @Resource
    private DashboardProjectStageController dashboardProjectStageController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_PROJECT_STAGE_CONTRACT.name();
    }

    @Override
    public DashboardProjectStageContractExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardProjectStageContractExcelExporter.class);
    }

    @Override
    public List<DashboardProjectStageContractModel> req2ExportList(FileExportREQ req) {
        DashboardProjectStageContractDetailREQ allREQ = BeanUtil.copyProperties(req.getExt(), DashboardProjectStageContractDetailREQ.class);
        R<ContractDetailSumRSP> sumRSPR = dashboardProjectStageController.contractList(allREQ);
        ContractDetailSumRSP data = sumRSPR.getData();
        List<DashboardProjectStageContractDetailRSP> lists = data.getList();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }

        List<DashboardProjectStageContractModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardProjectStageContractModel model = BeanUtil.copyProperties(rsp, DashboardProjectStageContractModel.class, "contractAmount", "leaseDuration", "interestRate");
            model.setLengthOfStay(valueUnitDTO2String(rsp.getLengthOfStay()));
            model.setContractAmount(valueUnitDTO2String(rsp.getContractAmount()));
            model.setLeaseDuration(valueUnitDTO2String(rsp.getLeaseDuration()));
            model.setInterestRate(valueUnitDTO2String(rsp.getInterestRate()));
            model.setLesseeNames(rsp.getClientName());
            model.setInterestRate(DashboardExportUtil.spliceHundred(DashboardExportUtil.valueUnitDTO2String(rsp.getInterestRate())));
            if(rsp.getApplyTime() != null){
                model.setApplyTime(rsp.getApplyTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            }
            list.add(model);
        });
        if (CollectionUtil.isEmpty(req.getIds())) {
            list.add(BeanUtil.copyProperties(data.getSumData(), DashboardProjectStageContractModel.class));
        }
        return list;
    }

    private String valueUnitDTO2String(ValueUnitDTO valueUnit){
        return Optional.ofNullable(valueUnit).map(ValueUnitDTO::getValue).map(e ->ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null);
    }

}
