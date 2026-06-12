package cn.zswltech.mithras.application.orchestration.adapter.basedata;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.basedata.job.BaseDataJobTodoPort;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BaseDataJobTodoPortAdapter implements BaseDataJobTodoPort {

    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    @Override
    public void createExchangeRateTodo(int year, int month, int dayOfMonth, LocalDateTime applyTime, List<Long> assigneeIds) {
        CommonProcessPrepare todo = CommonProcessPrepare.builder()
                .processType(BASE_DATA_EXCHANGE_RATE_TODO_PROCESS_TYPE)
                .formName(String.format("%s年%s月汇率设置", year, month))
                .currentNode("财务确认")
                .currentAssignee(JSONUtil.toJsonStr(assigneeIds))
                .applyTime(LocalDateTime.of(year, month, dayOfMonth, applyTime.getHour(), applyTime.getMinute(), applyTime.getSecond()))
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        commonProcessPrepareService.save(todo);
    }
}
