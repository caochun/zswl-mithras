package cn.zswltech.mithras.service.service.materialsfile.export.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardContractReturnListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardContractReturnListRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.controller.dashboard.DashboardOperationContractController;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.enums.dashboard.BusinessGroupEnum;
import cn.zswltech.mithras.service.enums.projestablish.ContractBusinessModelEnum;
import cn.zswltech.mithras.service.excel.exporter.dashboard.DashboardOperationContractReturnExcelExporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardOperationContractReturnModel;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class DashboardOperationContractReturnExportHandle extends ExportHandle<DashboardOperationContractReturnModel, DashboardOperationContractReturnExcelExporter> {

    @Resource
    private DashboardOperationContractController dashboardOperationContractController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_OPERATION_CONTRACT_RETURN.name();
    }

    @Override
    public DashboardOperationContractReturnExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardOperationContractReturnExcelExporter.class);
    }

    @Override
    public List<DashboardOperationContractReturnModel> req2ExportList(FileExportREQ req) {
        DashboardContractReturnListREQ businessReq = BeanUtil.copyProperties(req.getExt(), DashboardContractReturnListREQ.class);
        R<List<DashboardContractReturnListRSP>> mapR = dashboardOperationContractController.contractReturnList(businessReq);
        List<DashboardContractReturnListRSP> lists = mapR.getData();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardOperationContractReturnModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardOperationContractReturnModel model = BeanUtil.copyProperties(rsp, DashboardOperationContractReturnModel.class, "applyTime", "endTime");
            model.setBusinessGroup(Optional.ofNullable(BusinessGroupEnum.of(rsp.getBusinessGroup())).map(BusinessGroupEnum::display).orElse(null));
            model.setBusinessModel(Optional.ofNullable(ContractBusinessModelEnum.of(rsp.getBusinessModel())).map(ContractBusinessModelEnum::display).orElse(null));
            model.setApplyTime(getTimeString(rsp.getApplyTime()));
            model.setEndTime(getTimeString(rsp.getEndTime()));
            list.add(model);
        });
        return list;
    }

    private String getTimeString(LocalDateTime time){
        if(ObjectUtil.isEmpty(time)) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
    }

}
