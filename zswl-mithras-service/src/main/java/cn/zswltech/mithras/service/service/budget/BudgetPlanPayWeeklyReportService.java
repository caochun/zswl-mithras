package cn.zswltech.mithras.service.service.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
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
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.budget.BudgetStatusEnum;
import cn.zswltech.mithras.service.mapper.budget.BudgetPlanPayWeeklyReportMapper;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPayDetail;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPayWeeklyReport;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPayWeeklyReportDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
@Service
public class BudgetPlanPayWeeklyReportService extends ServiceImpl<BudgetPlanPayWeeklyReportMapper, BudgetPlanPayWeeklyReport> {

    @Resource
    private BudgetPlanPayWeeklyReportMapper budgetPlanPayWeeklyReportMapper;
    @Resource
    private BudgetPlanPayWeeklyReportDetailService budgetPlanPayWeeklyReportDetailService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    FlowProcessApiService processApiService;

    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;

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