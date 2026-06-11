package cn.zswltech.mithras.dashboard.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardApprovalListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardApprovalListRSP;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dashboard.controller.DashboardOperationContractController;
import cn.zswltech.mithras.foundation.enums.FileExportEnum;
import cn.zswltech.mithras.dashboard.enums.BusinessGroupEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ContractBusinessModelEnum;
import cn.zswltech.mithras.dashboard.excel.exporter.DashboardOperationApprovalExcelExporter;
import cn.zswltech.mithras.dashboard.excel.model.DashboardOperationApprovalModel;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.export.ExportHandle;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName DashboardClientOverviewAllExportHandle
 * @Description 业务工作台-融资视图-还本付息
 * @Author jackerhe
 * @Date 2024/7/8 3:14 下午
 * @Version 1.0
 **/
@Component
public class DashboardOperationApprovalExportHandle extends ExportHandle<DashboardOperationApprovalModel, DashboardOperationApprovalExcelExporter> {

    @Resource
    private DashboardOperationContractController dashboardOperationContractController;

    @Override
    public String getBusinessType() {
        return FileExportEnum.DASHBOARD_OPERATION_APPROVAL.name();
    }

    @Override
    public DashboardOperationApprovalExcelExporter getExportSimpleExporter() {
        return SpringContextHolder.getBean(DashboardOperationApprovalExcelExporter.class);
    }

    @Override
    public List<DashboardOperationApprovalModel> req2ExportList(FileExportREQ req) {
        DashboardApprovalListREQ businessReq = BeanUtil.copyProperties(req.getExt(), DashboardApprovalListREQ.class);
//        if(ObjectUtil.isNotEmpty(businessReq.getBusinessModel())) {
//            businessReq.setBusinessModel(businessReq.getBusinessModel().stream()
//                    .map(s -> s.replace("\"", "")) // 将每个字符串中的"替换为.
//                    .collect(Collectors.toList()));
//        }
//        if(ObjectUtil.isNotEmpty(businessReq.getBusinessGroup())) {
//            businessReq.setBusinessGroup(businessReq.getBusinessGroup().stream()
//                    .map(s -> s.replace("\"", "")) // 将每个字符串中的"替换为.
//                    .collect(Collectors.toList()));
//        }
        R<List<DashboardApprovalListRSP>> mapR = dashboardOperationContractController.approvalList(businessReq);
        List<DashboardApprovalListRSP> lists = mapR.getData();
        if (ObjectUtil.isEmpty(lists)) {
            return ListUtil.empty();
        }
        List<DashboardOperationApprovalModel> list = new ArrayList<>();
        lists.forEach(rsp -> {
            DashboardOperationApprovalModel model = BeanUtil.copyProperties(rsp, DashboardOperationApprovalModel.class, "applyTime",
                    "endTime", "operationHandlingArrivalTime", "operationHandlingSubmitTime", "operationReviewArrivalTime", "operationReviewSubmitTime");
            model.setBusinessGroup(Optional.ofNullable(BusinessGroupEnum.of(rsp.getBusinessGroup())).map(BusinessGroupEnum::display).orElse(null));
            model.setBusinessModel(Optional.ofNullable(ContractBusinessModelEnum.of(rsp.getBusinessModel())).map(ContractBusinessModelEnum::display).orElse(null));
            model.setApplyTime(getTimeString(rsp.getApplyTime()));
            model.setEndTime(getTimeString(rsp.getEndTime()));
            model.setOperationHandlingArrivalTime(getTimeString(rsp.getOperationHandlingArrivalTime()));
            model.setOperationHandlingSubmitTime(getTimeString(rsp.getOperationHandlingSubmitTime()));
            model.setOperationReviewArrivalTime(getTimeString(rsp.getOperationReviewArrivalTime()));
            model.setOperationReviewSubmitTime(getTimeString(rsp.getOperationReviewSubmitTime()));
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
