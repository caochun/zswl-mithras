package cn.zswltech.mithras.application.orchestration.adapter.associationreport.workflow;

import cn.zswltech.mithras.associationreport.service.job.AssociationReportProcessPrepareService;
import cn.zswltech.mithras.associationreport.service.job.AssociationReportTodoType;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AssociationReportProcessPrepareServiceAdapter implements AssociationReportProcessPrepareService {

    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    @Override
    public boolean saveTodo(AssociationReportTodoType todoType, Long applyId, String formName, String currentNode, List<Long> assigneeIds) {
        CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder()
                .processType(processType(todoType))
                .businessId(applyId.toString())
                .formName(formName)
                .currentNode(currentNode)
                .currentAssignee(assigneeIds.toString())
                .applyTime(LocalDateTime.now())
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        return commonProcessPrepareService.save(commonProcessPrepare);
    }

    @Override
    public boolean hasPendingTodo(AssociationReportTodoType todoType) {
        LambdaQueryWrapper<CommonProcessPrepare> query = Wrappers.lambdaQuery();
        query.eq(CommonProcessPrepare::getProcessType, processType(todoType));
        query.eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name());
        return commonProcessPrepareService.count(query) > 0;
    }

    private String processType(AssociationReportTodoType todoType) {
        if (todoType == AssociationReportTodoType.MAIN_BUSINESS) {
            return ProcessModelTypeEnum.AssociationReportMainBusinessFlow.name();
        }
        if (todoType == AssociationReportTodoType.QUARTER_MONTH) {
            return ProcessModelTypeEnum.AssociationReportQuarterMonthFlow.name();
        }
        throw new IllegalArgumentException("Unsupported association report todo type: " + todoType);
    }
}
