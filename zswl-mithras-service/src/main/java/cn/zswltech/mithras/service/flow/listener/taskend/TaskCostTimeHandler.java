package cn.zswltech.mithras.service.flow.listener.taskend;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.flow.core.flowlistener.task.TaskCompleteAction;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.flow.model.FlowTaskDuration;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataSpecialDate;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import cn.zswltech.mithras.workflow.application.flow.FlowTaskDurationService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.core.date.LocalDateTimeUtil.between;
import static cn.hutool.core.date.LocalDateTimeUtil.of;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.basedata.enums.BaseDataSpecialDateTypeEnum.HOLIDAY;
import static cn.zswltech.mithras.basedata.enums.BaseDataSpecialDateTypeEnum.WORKDAY;

/**
 * @author luyi
 */
@Slf4j
@Component
public class TaskCostTimeHandler implements TaskCompleteAction {
    @Resource
    private FlowTaskDurationService flowTaskDurationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void action(DelegateTask task) {
        LocalDateTime startTime = of(task.getCreateTime());
        LocalDateTime endTime = LocalDateTime.now();
        //
        List<BaseDataSpecialDate> specialDateList =
                getBean(BaseDataSpecialDateService.class).between(startTime.toLocalDate(), endTime.toLocalDate());
        //
        long workdayCount = specialDateList.stream().filter(e -> WORKDAY.name().equals(e.getSpecialType())).count();
        long holidayCount = specialDateList.stream().filter(e -> HOLIDAY.name().equals(e.getSpecialType())).count();

        //同一天算0天的，所以要加上1
        long betweenDays = between(startTime, endTime, ChronoUnit.DAYS) + 1;
        long betweenNormalWorkdays = betweenNormalWorkday(startTime, endTime);
        long betweenWorkdays = betweenNormalWorkdays + workdayCount - holidayCount;
        //save 2 db
        if (flowTaskDurationService.getByTaskId(task.getId()) != null) {
            log.error("耗时记录中已存在任务{}的数据，先删除，再插入", task.getId());
            flowTaskDurationService.removeByTaskIds(ListUtil.of(task.getId()));
        }
        FlowTaskDuration duration = new FlowTaskDuration();
        duration.setProcInstId(task.getProcessInstanceId());
        duration.setTaskId(task.getId());
        duration.setTaskDefKey(task.getTaskDefinitionKey());
        duration.setAssignee(isBlank(task.getAssignee()) ? null : Long.parseLong(task.getAssignee()));
        duration.setTaskStartAt(startTime);
        duration.setTaskEndAt(endTime);
        duration.setTaskCostDays((int) betweenDays);
        duration.setTaskCostWorkdays((int) betweenWorkdays);
        flowTaskDurationService.save(duration);
    }

    public void historyInit() {
        List<BaseDataSpecialDate> allSpecialDate = getBean(BaseDataSpecialDateService.class).list();
        long total = getBean(HistoryService.class).createHistoricTaskInstanceQuery().count();
        int page = 1, pageSize = 1000;
        do {
            int from = (page - 1) * pageSize;
            List<HistoricTaskInstance> tasks = getBean(HistoryService.class).createHistoricTaskInstanceQuery().listPage(from, pageSize);
            if (tasks.size() < pageSize) {
                break;
            }
            List<FlowTaskDuration> durations = new ArrayList<>(tasks.size());
            for (HistoricTaskInstance task : tasks) {
                FlowTaskDuration duration = new FlowTaskDuration();
                duration.setProcInstId(task.getProcessInstanceId());
                duration.setTaskId(task.getId());
                duration.setTaskDefKey(task.getTaskDefinitionKey());
                duration.setAssignee(isBlank(task.getAssignee()) ? null : Long.parseLong(task.getAssignee()));
                //
                LocalDateTime startTime = of(task.getCreateTime());
                LocalDateTime endTime = of(task.getEndTime());
                if (endTime == null) {
                    continue;
                }
                duration.setTaskStartAt(startTime);
                duration.setTaskEndAt(endTime);
                List<BaseDataSpecialDate> specialDateList = allSpecialDate.stream().filter(
                                e -> !e.getSpecialDate().isBefore(startTime.toLocalDate()) && !e.getSpecialDate().isAfter(endTime.toLocalDate()))
                        .collect(Collectors.toList());
                //
                long workdayCount = specialDateList.stream().filter(e -> WORKDAY.name().equals(e.getSpecialType())).count();
                long holidayCount = specialDateList.stream().filter(e -> HOLIDAY.name().equals(e.getSpecialType())).count();

                //同一天算0天的，所以要加上1
                long betweenDays = between(startTime, endTime, ChronoUnit.DAYS) + 1;
                long betweenNormalWorkdays = betweenNormalWorkday(startTime, endTime);
                long betweenWorkdays = betweenNormalWorkdays + workdayCount - holidayCount;

                duration.setTaskCostDays((int) betweenDays);
                duration.setTaskCostWorkdays((int) betweenWorkdays);
                durations.add(duration);
            }

            flowTaskDurationService.removeByTaskIds(durations.stream().map(FlowTaskDuration::getTaskId).collect(Collectors.toList()));
            flowTaskDurationService.saveBatch(durations);
            log.info("总共{}条task，已完成{}条", total, from + pageSize);
            page++;
        } while (true);
        log.info("历史任务耗时初始化完成");
    }

    /**
     * 统计两个日期之间的天数，不包括周六，周日
     */
    private static Long betweenNormalWorkday(LocalDateTime start, LocalDateTime end) {
        LocalDate from = start.toLocalDate();
        LocalDate to = end.toLocalDate();
        Long count = 0L;
        while (!from.isAfter(to)) {
            int dayOfWeek = from.getDayOfWeek().getValue();
            //星期一到星期五的才算
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                count++;
            }
            from = from.plusDays(1);
        }
        return count;
    }


}
