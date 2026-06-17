package cn.zswltech.mithras.application.orchestration.budget;

import cn.zswltech.mithras.budget.application.port.BudgetPlanPayWeeklyJobPort;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.budget.application.BudgetPlanPayWeeklyReportApplicationService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportAddREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportListREQ;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportListRSP;
import cn.zswltech.mithras.dto.budget.weekly.BudgetPlanPayWeeklyReportRemoveREQ;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.budget.enums.BudgetPlanTypeEnum;
import cn.zswltech.mithras.budget.enums.BudgetStatusEnum;
import cn.zswltech.mithras.budget.mapper.BudgetPlanPayWeeklyReportMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlan;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPay;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetail;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayWeeklyReport;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayWeeklyReportDetail;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.basedata.util.WorkdayWeekUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-投放计划-项目周报
 * @author vico
 * @date 2025-04-11
*/
@Slf4j
@Service
public class BudgetPlanPayWeeklyReportService extends ServiceImpl<BudgetPlanPayWeeklyReportMapper, BudgetPlanPayWeeklyReport> implements BudgetPlanPayWeeklyReportApplicationService, BudgetPlanPayWeeklyJobPort {

    @Resource
    private BudgetPlanPayWeeklyReportMapper budgetPlanPayWeeklyReportMapper;
    @Resource
    private BudgetPlanPayWeeklyReportDetailService budgetPlanPayWeeklyReportDetailService;
    @Resource
    private BudgetPlanService budgetPlanService;
    @Resource
    private BudgetPlanPayService budgetPlanPayService;
    @Resource
    private BudgetPlanPayDetailService budgetPlanPayDetailService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    FlowProcessApiService processApiService;

    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createBudgetPlanPayWeekly(LocalDate targetDate) {
        if (!WorkdayWeekUtil.isDateFirstWorkday(targetDate)) {
            log.info("BudgetPlanPayWeeklyJob createBudgetPlanPayWeekly {} 非第一个工作日跳过", targetDate);
            return;
        }
        BudgetPlan budgetPlan = budgetPlanService.getOne(
                Wrappers.<BudgetPlan>lambdaQuery()
                        .eq(BudgetPlan::getPlanYear, targetDate.getYear())
                        .eq(BudgetPlan::getPlanMonth, targetDate.getMonth())
                        .eq(BudgetPlan::getBudgetStatus, BudgetStatusEnum.CONFIRM.name())
                        .in(BudgetPlan::getBudgetType, ListUtil.of(BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name()))
                        .orderByDesc(BudgetPlan::getId)
                        .last(StringUtil.mysqlLimitOne())
        );
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getByBudgetPlanId(budgetPlan.getId());
        BudgetPlanPayWeeklyReport budgetPlanPayWeeklyReport = this.getOne(
                Wrappers.<BudgetPlanPayWeeklyReport>lambdaQuery()
                        .ge(BudgetPlanPayWeeklyReport::getDateFrom, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1))
                        .le(BudgetPlanPayWeeklyReport::getDateTo, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth()))
                        .eq(BudgetPlanPayWeeklyReport::getPlanStatus, BudgetStatusEnum.COLLECT_FINISH.name())
                        .orderByDesc(BudgetPlanPayWeeklyReport::getDateFrom)
                        .last(StringUtil.mysqlLimitOne())
        );
        List<BudgetPlanPayWeeklyReportDetail> todoList;
        if (Objects.isNull(budgetPlanPay) && Objects.isNull(budgetPlanPayWeeklyReport)) {
            log.info("没有投放计划也没有历史周报，不生成周报待办");
            return;
        } else if (Objects.isNull(budgetPlanPay)) {
            log.info("使用上一次周报数据生成周报待办[{}]", JSONUtil.toJsonStr(budgetPlanPayWeeklyReport));
            List<BudgetPlanPayWeeklyReportDetail> budgetPlanPayWeeklyReportDetailList = budgetPlanPayWeeklyReportDetailService.listByReportId(budgetPlanPayWeeklyReport.getId());
            todoList = BeanUtil.copyToList(budgetPlanPayWeeklyReportDetailList, BudgetPlanPayWeeklyReportDetail.class);
        } else if (Objects.isNull(budgetPlanPayWeeklyReport)) {
            log.info("使用投放计划数据生成周报待办[{}]", JSONUtil.toJsonStr(budgetPlanPay));
            List<BudgetPlanPayDetail> budgetPlanPayDetailList = budgetPlanPayDetailService.listByBudgetPlanId(budgetPlanPay.getBudgetPlanId());
            todoList = BeanUtil.copyToList(budgetPlanPayDetailList, BudgetPlanPayWeeklyReportDetail.class);
        } else {
            if (budgetPlanPay.getCreateTime().isAfter(budgetPlanPayWeeklyReport.getCreateTime())) {
                log.info("使用投放计划数据生成周报待办[{}]", JSONUtil.toJsonStr(budgetPlanPay));
                List<BudgetPlanPayDetail> budgetPlanPayDetailList = budgetPlanPayDetailService.listByBudgetPlanId(budgetPlanPay.getBudgetPlanId());
                todoList = BeanUtil.copyToList(budgetPlanPayDetailList, BudgetPlanPayWeeklyReportDetail.class);
            } else {
                log.info("使用上一次周报的数据生成周报待办[{}]", JSONUtil.toJsonStr(budgetPlanPayWeeklyReport));
                List<BudgetPlanPayWeeklyReportDetail> budgetPlanPayWeeklyReportDetailList = budgetPlanPayWeeklyReportDetailService.listByReportId(budgetPlanPayWeeklyReport.getId());
                todoList = BeanUtil.copyToList(budgetPlanPayWeeklyReportDetailList, BudgetPlanPayWeeklyReportDetail.class);
            }
        }
        WorkdayWeekUtil.WorkweekResult workweekResult = WorkdayWeekUtil.calculateWorkweek(targetDate);
        todoList.removeIf(e -> this.isBeforeCurrentMonth(targetDate, e.getPlanPayDate()) || LongUtil.null2zero(e.getPaidAmount()) >= LongUtil.null2zero(e.getPlanPayAmount()));
        if (CollectionUtil.isEmpty(todoList)) {
            log.info("{}-{}不存在符合条件的待发周报", LocalDateTimeUtil.format(workweekResult.getFirstWorkday(), DatePattern.NORM_DATE_PATTERN), LocalDateTimeUtil.format(workweekResult.getLastWorkday(), DatePattern.NORM_DATE_PATTERN));
            return;
        }
        Map<Long, Set<Long>> deptId2UserIds = new HashMap<>();
        for (BudgetPlanPayWeeklyReportDetail reportDetail : todoList) {
            if (Objects.isNull(reportDetail.getProjReviewId())) {
                continue;
            }
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(reportDetail.getProjReviewId());
            if (Objects.nonNull(projReviewBaseInfo)) {
                reportDetail.setSponsorUserId(projReviewBaseInfo.getProjSponsorUserId());
                reportDetail.setBelongDeptId(projReviewBaseInfo.getBizDeptId());
            }
            Set<Long> userIds = deptId2UserIds.get(reportDetail.getBelongDeptId());
            if (Objects.isNull(userIds)) {
                userIds = new HashSet<>();
                deptId2UserIds.put(reportDetail.getBelongDeptId(), userIds);
            }
            userIds.add(reportDetail.getSponsorUserId());
        }
        BudgetPlanPayWeeklyReportAddREQ budgetPlanPayWeeklyReportAddREQ = BeanUtil.copyProperties(budgetPlanPay, BudgetPlanPayWeeklyReportAddREQ.class);
        budgetPlanPayWeeklyReportAddREQ.setDateFrom(workweekResult.getFirstWorkday());
        budgetPlanPayWeeklyReportAddREQ.setDateTo(workweekResult.getLastWorkday());
        budgetPlanPayWeeklyReportAddREQ.setPlanStatus(BudgetStatusEnum.COLLECTING.name());
        budgetPlanPayWeeklyReportAddREQ.setBudgetPlanPayId(budgetPlanPay.getId());
        Long budgetPlanPayWeeklyId = this.create(budgetPlanPayWeeklyReportAddREQ, todoList);
        this.createProcess(deptId2UserIds, budgetPlanPayWeeklyId);
    }

    private boolean isBeforeCurrentMonth(LocalDate targetDate, LocalDate date) {
        if (ObjectUtil.isEmpty(date)) {
            return false;
        }
        LocalDate targetDateBegin = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1);
        return date.isBefore(targetDateBegin);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long create(BudgetPlanPayWeeklyReportAddREQ req, List<BudgetPlanPayWeeklyReportDetail> detailList) {
        BudgetPlanPayWeeklyReport info = BeanUtil.copyProperties(req, BudgetPlanPayWeeklyReport.class);
        budgetPlanPayWeeklyReportMapper.insert(info);
        // 创建详情
        if(ObjectUtil.isNotEmpty(detailList)) {
            detailList.forEach(e -> {
                e.setId(null);
                e.reset();
                e.setBudgetPlanWeeklyReportId(info.getId());
                e.setIsBusinessheadConfirm(YesOrNoNumberEnum.NO.getCode());
                e.setBusinessheadConfirmDate(LocalDate.now());
            });
            budgetPlanPayWeeklyReportDetailService.saveBatch(detailList);
        }
        return info.getId();
    }

    //创建流程
    @Transactional(rollbackFor = Throwable.class)
    public void createProcess(Map<Long, Set<Long>> deptId2UserIds, Long budgetPlanPayWeeklyId) {
        BudgetPlanPayWeeklyReport budgetPlanPayWeeklyReport = this.getById(budgetPlanPayWeeklyId);
        if (ObjectUtil.isEmpty(budgetPlanPayWeeklyReport)) {
            return;
        }
        //需要按照部门发送流程
        deptId2UserIds.forEach((deptId, userIds) -> {
            Long userIdByOrgJob = sysUserService.getUserIdByOrgJob(deptId, deptLeaderJob);
            String weekProcessKey = getWeekProcessKey(budgetPlanPayWeeklyId, deptId);
            StartProcessReq startProcessReq = new StartProcessReq();
            startProcessReq.setBusinessKey(weekProcessKey);
            String s = String.format("%s-%s投放计划周报", LocalDateTimeUtil.format(budgetPlanPayWeeklyReport.getDateFrom(), DatePattern.NORM_DATE_PATTERN), LocalDateTimeUtil.format(budgetPlanPayWeeklyReport.getDateTo(), DatePattern.NORM_DATE_PATTERN));
            startProcessReq.setProcessInstanceName(s);
            startProcessReq.setStartUserId(String.valueOf(GlobalConstants.READONLY_ID));
            startProcessReq.setStartUserDeptId(String.valueOf(deptId));
            //增加法律合规部负责人
            Set<String> detpSet = new HashSet<>();
            detpSet.add(String.valueOf(userIdByOrgJob));
            Map<String, Object> map = new HashMap<>();
            map.put("userTask_projectmanager", userIds.stream().map(String::valueOf).collect(Collectors.toList()));
            map.put("userTask_deptMaster", new ArrayList<>(detpSet));
            startProcessReq.setVariables(map);
            startProcessReq.setModelKey(ProcessModelTypeEnum.BudgetPlanPayWeeklyFlow.name());
            processApiService.start(startProcessReq);
        });
    }

    private String getWeekProcessKey(Long budgetPlanWeeklyReportId, Long deptId) {
        return String.format("%s-%s", budgetPlanWeeklyReportId, deptId);
    }

    //创建流程
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(String businessKey, String processId, Integer endType) {
        String[] split = businessKey.split("-");
        if (split.length != 2) {
            return;
        }
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        Long budgetPlanWeeklyReportId = Long.valueOf(split[0]);
        Long deptId = Long.valueOf(split[1]);
        BudgetPlanPayWeeklyReport budgetPlanPayWeeklyReport = this.getById(budgetPlanWeeklyReportId);
        List<BudgetPlanPayWeeklyReportDetail> reportDetails = budgetPlanPayWeeklyReportDetailService.list(Wrappers.<BudgetPlanPayWeeklyReportDetail>lambdaQuery()
                .eq(BudgetPlanPayWeeklyReportDetail::getBudgetPlanWeeklyReportId, budgetPlanWeeklyReportId)
                .eq(BudgetPlanPayWeeklyReportDetail::getBelongDeptId, deptId));
        if (processPass) {
            //通过
            reportDetails.forEach(e -> {
                e.setIsBusinessheadConfirm(YesOrNoNumberEnum.YES.getCode());
                e.setBusinessheadConfirmDate(LocalDate.now());
            });
            budgetPlanPayWeeklyReportDetailService.updateBatchById(reportDetails);
            // 所有部门负责人都确认后状态变为"收集完成"
            List<OrgDO> bizDeptList = sysUserService.listBizDept();
            List<String> businessKeyList = bizDeptList.stream().map(e -> budgetPlanWeeklyReportId + "-" + e.getId()).collect(Collectors.toList());
            // 去掉自己
            businessKeyList.removeIf(e -> Objects.equals(e, businessKey));
            boolean finish = true;
            if (CollectionUtil.isNotEmpty(businessKeyList)) {
                // 查其他的流程
                ProcessPageReq processPageReq = new ProcessPageReq();
                processPageReq.setPageIndex(1);
                processPageReq.setPageSize(Integer.MAX_VALUE);
                processPageReq.setBusinessKeyList(businessKeyList);
                processPageReq.setModelKey(ProcessModelTypeEnum.BudgetPlanPayWeeklyFlow.name());
                cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(processPageReq);
                if (CollectionUtil.isNotEmpty(processRespPage.getContents())) {
                    for (ProcessResp processResp : processRespPage.getContents()) {
                        if (!ProcessBusinessStatusEnum.success(processResp.getProcessStatus())) {
                            finish = false;
                        }
                    }
                }
            }
            if (finish) {
                budgetPlanPayWeeklyReport.setPlanStatus(BudgetStatusEnum.COLLECT_FINISH.name());
                budgetPlanPayWeeklyReportMapper.updateById(budgetPlanPayWeeklyReport);
            }
        } else {
            throw new MithrasException("暂不支持的操作");
        }
    }

    public Page<BudgetPlanPayWeeklyReport> list(BudgetPlanPayWeeklyReportListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<BudgetPlanPayWeeklyReport>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getPlanStatus()), BudgetPlanPayWeeklyReport::getPlanStatus, req.getPlanStatus())
                .like(ObjectUtil.isNotEmpty(req.getBudgetPlanName()), BudgetPlanPayWeeklyReport::getBudgetPlanName, req.getBudgetPlanName())
                .orderByDesc(BudgetPlanPayWeeklyReport::getId));
    }

    public PageR<BudgetPlanPayWeeklyReportListRSP> pageList(BudgetPlanPayWeeklyReportListREQ req) {
        Page<BudgetPlanPayWeeklyReport> data = this.list(req);
        List<BudgetPlanPayWeeklyReportListRSP> list = BeanUtil.copyToList(data.getRecords(), BudgetPlanPayWeeklyReportListRSP.class);
        return PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize());
    }

    public BudgetPlanPayWeeklyReportListRSP planInfo(Long id) {
        return BeanUtil.copyProperties(this.getById(id), BudgetPlanPayWeeklyReportListRSP.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(BudgetPlanPayWeeklyReportRemoveREQ req) {
        BudgetPlanPayWeeklyReport originalInfo = budgetPlanPayWeeklyReportMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        budgetPlanPayWeeklyReportMapper.deleteById(req.getId());
    }

    public List<BudgetPlanPayWeeklyReport> getMonthWeek(Integer planYear, Integer planMonth) {
        LocalDate start = LocalDate.of(planYear, planMonth, 1);
        LocalDate end = start.plusMonths(1);
        return this.list(Wrappers.<BudgetPlanPayWeeklyReport>lambdaQuery()
        .ge(BudgetPlanPayWeeklyReport::getDateFrom, start)
        .lt(BudgetPlanPayWeeklyReport::getDateFrom, end));
    }

}
