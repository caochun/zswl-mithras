package cn.zswltech.mithras.others.数据导出;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataSpecialDate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DateUtil.parse;
import static cn.hutool.core.date.LocalDateTimeUtil.between;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yibin
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 审批流耗时 {
    Path basePath = Paths.get(System.getProperty("user.home"), "Desktop");

    private List<BaseDataSpecialDate> dateList;
    Map<LocalDate, String> specialDateMap = new HashMap<>();

    @BeforeEach
    public void init() {
        dateList = getBean(BaseDataSpecialDateService.class).list();
        specialDateMap = dateList.stream()
                .collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate, BaseDataSpecialDate::getSpecialType));

    }

    @Test
    public void cost() {
        List<String> list = ListUtil.of("ProjReviewCreateFlow", "ProjReviewModifyFlow");
//        List<String> list = ListUtil.of("PaymentCreateFlow", "PaymentModifyFlow");
        DateTime start = parse("2023-04-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        DateTime end = parse("2023-06-30 23:59:59", "yyyy-MM-dd HH:mm:ss");
//        评审(start, end);
        付款(start, end);

      /*  start = parse("2023-06-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        end = parse("2023-06-30 00:00:00", "yyyy-MM-dd HH:mm:ss");
        评审(start, end);
        付款(start, end);*/
    }

    private void 付款(Date from, Date to) {
        HistoricProcessInstanceQuery instanceQuery = getBean(HistoryService.class).createHistoricProcessInstanceQuery();
        instanceQuery.startedAfter(from);
        instanceQuery.startedBefore(to);
        instanceQuery.processDefinitionKeyIn(ListUtil.of("PaymentCreateFlow", "PaymentModifyFlow"));
        List<HistoricProcessInstance> list = instanceQuery.list();
        Map<String, Map<String, TimeInfo>> result = new HashMap<>();
        for (HistoricProcessInstance processInstance : list) {
            Map<String, TimeInfo> map = new HashMap<>();
            String businessKey = processInstance.getBusinessKey();
            List<HistoricTaskInstance> taskList = getBean(HistoryService.class).createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId()).list();
            for (HistoricTaskInstance task : taskList) {
                Date createTime = task.getCreateTime();
                Date endTime = task.getEndTime();
                String taskName = task.getName();
                LocalDateTime start = LocalDateTimeUtil.of(createTime);
                if (null == endTime) {
                    continue;
                }
                LocalDateTime end = LocalDateTimeUtil.of(endTime);
                TimeInfo timeInfo = map.get(taskName);
                if (timeInfo == null) {
                    timeInfo = new TimeInfo(taskName, null, null, start, end);
                    map.put(taskName, timeInfo);
                } else {
                    timeInfo.setStart(timeInfo.getStart().isAfter(start) ? start : timeInfo.getStart());
                    timeInfo.setEnd(timeInfo.getEnd().isAfter(end) ? timeInfo.getEnd() : end);
                }
            }
            //
            result.put(businessKey, map);
        }
        //所有的审批节点
        Set<String> allTaskNames = new LinkedHashSet<>();
        result.values().forEach(e -> allTaskNames.addAll(e.keySet()));
        //用于补充项目名称和部门
        Map<Long, PaymentBaseInfo> paymentMap = getBean(PaymentBaseInfoService.class).listByIds(result.keySet().stream().map(Long::valueOf).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(PaymentBaseInfo::getId, e -> e));
        Map<Long, ContractBaseInfo> contractMap = getBean(ContractBaseInfoService.class).listByIds(paymentMap.values().stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e));
        Map<Long, ProjReviewBaseInfo> reviewMap = getBean(ProjReviewBaseInfoService.class).listByIds(contractMap.values().stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, e -> e));
        Map<Long, String> orgNameMap = getBean(OrgDOMapper.class).selectByIds(reviewMap.values().stream().map(ProjReviewBaseInfo::getBizDeptId).collect(Collectors.toList()), null)
                .stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName));


        StringBuilder builder = new StringBuilder();
        builder.append("部门名称").append(",");
        builder.append("项目名称").append(",");
        for (String taskName : allTaskNames) {
            builder.append(taskName + "-耗时（分钟）,");
        }
        builder.append(System.lineSeparator());
        result.forEach((k, v) -> {
            builder.append(orgNameMap.get(reviewMap.get(contractMap.get(paymentMap.get(Long.valueOf(k)).getContractId()).getProjReviewId()).getBizDeptId())).append(",");
            builder.append(reviewMap.get(contractMap.get(paymentMap.get(Long.valueOf(k)).getContractId()).getProjReviewId()).getProjName()).append(",");
            for (String taskName : allTaskNames) {
                TimeInfo timeInfo = v.get(taskName);
                if (null == timeInfo) {
                    timeInfo = new TimeInfo();
                }
                String between = "";
                if (isNotNull(timeInfo.start) || isNotNull(timeInfo.getEnd())) {
                    between = String.valueOf(between(timeInfo.getStart(), timeInfo.getEnd()).toMinutes());
                }
                builder.append(between).append(",");
            }
            builder.append(System.lineSeparator());
        });
        System.out.println(builder);
    }

    private void 评审(Date from, Date to) {
        HistoricProcessInstanceQuery instanceQuery = getBean(HistoryService.class).createHistoricProcessInstanceQuery();
        instanceQuery.startedAfter(from);
        instanceQuery.startedBefore(to);
        instanceQuery.processDefinitionKeyIn(ListUtil.of("ProjReviewCreateFlow", "ProjReviewModifyFlow"));
        List<HistoricProcessInstance> list = instanceQuery.list();
        Map<String, Map<String, TimeInfo>> result = new HashMap<>();
        for (HistoricProcessInstance processInstance : list) {
            Map<String, TimeInfo> map = new HashMap<>();
            String businessKey = processInstance.getBusinessKey();
            List<HistoricTaskInstance> taskList = getBean(HistoryService.class).createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId()).list();
            for (HistoricTaskInstance task : taskList) {
                Date createTime = task.getCreateTime();
                Date endTime = task.getEndTime();
                String taskName = task.getName();
                LocalDateTime start = LocalDateTimeUtil.of(createTime);
                if (null == endTime) {
                    continue;
                }
                LocalDateTime end = LocalDateTimeUtil.of(endTime);
                TimeInfo timeInfo = map.get(taskName);
                if (timeInfo == null) {
                    timeInfo = new TimeInfo(taskName, null, null, start, end);
                    map.put(taskName, timeInfo);
                } else {
                    timeInfo.setStart(timeInfo.getStart().isAfter(start) ? start : timeInfo.getStart());
                    timeInfo.setEnd(timeInfo.getEnd().isAfter(end) ? timeInfo.getEnd() : end);
                }
            }
            //
            result.put(businessKey, map);
        }
        //所有的审批节点
        Set<String> allTaskNames = new LinkedHashSet<>();
        result.values().forEach(e -> allTaskNames.addAll(e.keySet()));
        //用于补充项目名称和部门
        Map<Long, ProjReviewBaseInfo> reviewMap = getBean(ProjReviewBaseInfoService.class).listByIds(result.keySet().stream().map(Long::valueOf).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, e -> e));

        Map<Long, String> orgNameMap = getBean(OrgDOMapper.class).selectByIds(reviewMap.values().stream().map(ProjReviewBaseInfo::getBizDeptId).collect(Collectors.toList()), null)
                .stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName));


        StringBuilder builder = new StringBuilder();
        builder.append("部门名称").append(",");
        builder.append("项目名称").append(",");
        for (String taskName : allTaskNames) {
            builder.append(taskName + "-耗时（分钟）,");
        }
        builder.append(System.lineSeparator());
        result.forEach((k, v) -> {
            builder.append(orgNameMap.get(reviewMap.get(Long.valueOf(k)).getBizDeptId())).append(",");
            builder.append(reviewMap.get(Long.valueOf(k)).getProjName()).append(",");
            for (String taskName : allTaskNames) {
                TimeInfo timeInfo = v.get(taskName);
                if (null == timeInfo) {
                    timeInfo = new TimeInfo();
                }
                String between = "";
                if (isNotNull(timeInfo.start) || isNotNull(timeInfo.getEnd())) {
                    between = String.valueOf(between(timeInfo.getStart(), timeInfo.getEnd()).toMinutes());
                }
                builder.append(between).append(",");
            }
            builder.append(System.lineSeparator());
        });
        System.out.println(builder);
    }

/*
    private void 耗时(List<String> flows) {
        HistoricProcessInstanceQuery instanceQuery = getBean(HistoryService.class).createHistoricProcessInstanceQuery();
        instanceQuery.startedAfter(parse("2023-04-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        instanceQuery.startedBefore(parse("2023-06-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        instanceQuery.processDefinitionKeyIn(flows);
        List<HistoricProcessInstance> list = instanceQuery.list();
        Set<String> taskNameSet = new HashSet<>();
        Map<String, Map<String, TimeInfo>> result = new HashMap<>();
        for (HistoricProcessInstance processInstance : list) {
            Map<String, TimeInfo> map = new HashMap<>();
            String instanceName = processInstance.getName();
            String processName = processInstance.getProcessDefinitionName();

            List<HistoricTaskInstance> taskList = getBean(HistoryService.class).createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId()).list();
            for (HistoricTaskInstance task : taskList) {
                Date createTime = task.getCreateTime();
                Date endTime = task.getEndTime();
                String taskName = task.getName();
                LocalDate start = LocalDateTimeUtil.of(createTime).toLocalDate();
                if (null == endTime) {
                    continue;
                }
                taskNameSet.add(taskName);
                LocalDate end = LocalDateTimeUtil.of(endTime).toLocalDate();
                TimeInfo timeInfo = map.get(taskName);
                if (timeInfo == null) {
                    timeInfo = new TimeInfo(taskName, null, null, start, end);
                    map.put(taskName, timeInfo);
                } else {
                    timeInfo.setStart(timeInfo.getStart().isAfter(start) ? start : timeInfo.getStart());
                    timeInfo.setEnd(timeInfo.getEnd().isAfter(end) ? timeInfo.getEnd() : end);
                }
            }
            //
            for (TimeInfo value : map.values()) {
                int days = 0;
                int workdays = 0;
                LocalDate start = value.getStart();
                LocalDate end = value.getEnd();
                while (!start.isAfter(end)) {
                    days++;
                    if (!isNotWorkday(start)) {
                        workdays++;
                    }
                    start = start.plusDays(1);
                }
                value.setDays(days);
                value.setWorkdays(workdays);
            }
            result.put(instanceName, map);
        }
        //
        Set<String> allTaskNames = new LinkedHashSet<>();
        result.values().forEach(e -> allTaskNames.addAll(e.keySet()));

        StringBuilder builder = new StringBuilder();
        builder.append("实例名称").append(",");
        for (String taskName : allTaskNames) {
            builder.append(taskName + "-耗时（自然日）,");
            builder.append(taskName + "-耗时（工作日）,");
            builder.append(taskName + "-（开始时间）,");
            builder.append(taskName + "-（结束时间）,");
        }
        builder.append(System.lineSeparator());
        result.forEach((k, v) -> {
            builder.append(k).append(",");
            for (String taskName : allTaskNames) {
                TimeInfo timeInfo = v.get(taskName);
                if (null == timeInfo) {
                    timeInfo = new TimeInfo();
                }
                builder.append(timeInfo.getDays()).append(",");
                builder.append(timeInfo.getWorkdays()).append(",");
                builder.append(LocalDateTimeUtil.format(timeInfo.getStart(), "yyyy-MM-dd HH:mm:ss")).append(",");
                builder.append(LocalDateTimeUtil.format(timeInfo.getEnd(), "yyyy-MM-dd HH:mm:ss")).append(",");
            }
            builder.append(System.lineSeparator());
        });
        System.out.println(builder);


    }*/

    private boolean isNotWorkday(LocalDate date) {
        return date.getDayOfWeek().getValue() > 5 && (specialDateMap.get(date) == null) ||
                date.getDayOfWeek().getValue() <= 5 && (null != specialDateMap.get(date) && specialDateMap.get(date).equals("HOLIDAY"));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeInfo {
        private String taskName;
        private Integer days;
        private Integer workdays;
        private LocalDateTime start;
        private LocalDateTime end;
    }
}
