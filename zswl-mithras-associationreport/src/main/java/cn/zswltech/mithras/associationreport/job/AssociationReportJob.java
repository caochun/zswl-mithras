package cn.zswltech.mithras.associationreport.job;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.associationreport.StoreDataSelector;
import cn.zswltech.mithras.associationreport.service.AssociationReportApplyService;
import cn.zswltech.mithras.associationreport.storedata.DataStore;
import cn.zswltech.mithras.dto.associationreport.AssociationReportCreateREQ;
import cn.zswltech.mithras.foundation.enums.DataSource;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationProcessStatusEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportPeriodCategoryEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportStatusEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReportApply;
import cn.zswltech.mithras.associationreport.service.job.AssociationReportJobService;
import cn.zswltech.mithras.associationreport.service.job.AssociationReportProcessPrepareService;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author dingqi
 * @date 2025/9/14
 * @description 金融局报送批处理任务
 */
@Slf4j
@Component
public class AssociationReportJob {
    @Resource
    private AssociationReportJobService associationReportJobService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private AssociationReportApplyService associationReportApplyService;
    @Resource
    private AssociationReportProcessPrepareService processPrepareService;
    @Resource
    private SysUserService sysUserService;

    @XxlJob("collectDataFromSystem")
    public void collectDataFromSystem() {
        LocalDate jobExecDate = LocalDate.now();
//        LocalDate jobExecDate = LocalDate.of(2025, 10, 1);
        if (StrUtil.isNotBlank(XxlJobHelper.getJobParam())) {
            jobExecDate = LocalDateTimeUtil.parseDate(XxlJobHelper.getJobParam(), DatePattern.NORM_DATE_PATTERN);
        }
        // 循环处理
        for (AssociationReportCategoryEnum reportCategoryEnum : AssociationReportCategoryEnum.values()) {
            if (!reportCategoryEnum.isCollectDataFromSystem()) {
                continue;
            }
            // 如果是季报类型则需判断当前月份是季度首月
            if (reportCategoryEnum.getPeriod() == AssociationReportPeriodCategoryEnum.QUARTER && !this.isQuarterFirstMonth(jobExecDate)) {
                log.info("金融局报送【{}】自动取值，跑批任务月份非季度首月，不执行季报的自动取值任务", reportCategoryEnum.getDisplay());
                continue;
            }
            LocalDate targetDate = this.ensureLastPeriodLastDay(jobExecDate, reportCategoryEnum.getPeriod());
            try {
                // 判断该周期是否已经存在报送记录（不区分状态），如果有的话就不生成待报送记录了
                boolean exist = this.isExistReportRecord(reportCategoryEnum, targetDate);
                if (exist) {
                    log.info("金融局报送【{}】自动取值，已存在报送记录，无需执行任务[targetDate:{}]", reportCategoryEnum.getDisplay(), LocalDateTimeUtil.format(targetDate, DatePattern.NORM_DATE_PATTERN));
                    continue;
                }
                // 判断该类型报表取值前置数据是否准备妥当，如果前置数据还未准备好则等下一次任务
                boolean prepare = this.isPrepareData(reportCategoryEnum, targetDate);
                if (!prepare) {
                    log.info("金融局报送【{}】自动取值，前置数据还未准备好，无需执行任务[targetDate:{}]", reportCategoryEnum.getDisplay(), LocalDateTimeUtil.format(targetDate, DatePattern.NORM_DATE_PATTERN));
                    continue;
                }
                // 创建待报送记录并自动取值
                AssociationReportCreateREQ req = new AssociationReportCreateREQ();
                req.setReportCategoryCode(reportCategoryEnum.name());
                req.setPeriodCategory(reportCategoryEnum.getPeriod().name());
                req.setYear(targetDate.getYear());
                if (reportCategoryEnum.getPeriod() == AssociationReportPeriodCategoryEnum.MONTH) {
                    req.setPeriod(targetDate.getMonthValue());
                }
                if (reportCategoryEnum.getPeriod() == AssociationReportPeriodCategoryEnum.QUARTER) {
                    req.setPeriod(DateUtil.ensureQuarter(targetDate.getMonthValue()));
                }
                req.setDataSource(DataSource.SYSTEM.getDisplay());
                req.setIsShow(YesOrNoNumberEnum.NO.getCode());
                req.setCheckExist(Boolean.FALSE);
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    try {
                        String reportInstanceId = associationReportJobService.create(req);
                        StoreDataSelector.getInstance(reportCategoryEnum.name()).storeFromSystemJob(reportInstanceId);
                    } catch (Exception e) {
                        transactionStatus.setRollbackOnly();
                        log.error("金融局报送【{}】自动取值数据保存异常[targetDate:{}]", reportCategoryEnum.getDisplay(), LocalDateTimeUtil.format(targetDate, DatePattern.NORM_DATE_PATTERN), e);
                    }
                });
            } catch (Exception e) {
                log.error("金融局报送【{}】自动取值任务执行异常[targetDate:{}]", reportCategoryEnum.getDisplay(), LocalDateTimeUtil.format(targetDate, DatePattern.NORM_DATE_PATTERN), e);
            }
        }
        // 尝试生成待办任务
        this.generateTodo(jobExecDate);
    }

    private boolean isExistReportRecord(AssociationReportCategoryEnum reportCategoryEnum, LocalDate targetDate) {
        LambdaQueryWrapper<AssociationReport> query = Wrappers.lambdaQuery();
        query.eq(AssociationReport::getReportCategoryCode, reportCategoryEnum.name());
        query.eq(AssociationReport::getDataSource, DataSource.SYSTEM.getDisplay());
        query.eq(AssociationReport::getReportYear, targetDate.getYear());
        if (reportCategoryEnum.getPeriod() == AssociationReportPeriodCategoryEnum.MONTH) {
            query.eq(AssociationReport::getReportPeriod, targetDate.getMonthValue());
        } else if (reportCategoryEnum.getPeriod() == AssociationReportPeriodCategoryEnum.QUARTER) {
            query.eq(AssociationReport::getReportPeriod, DateUtil.ensureQuarter(targetDate.getMonthValue()));
        } else {
            throw new MithrasException("报表周期类型非月度或季度");
        }
        return associationReportJobService.count(query) > 0;
    }

    private boolean isPrepareData(AssociationReportCategoryEnum reportCategoryEnum, LocalDate targetDate) {
        DataStore dataStore = StoreDataSelector.getInstance(reportCategoryEnum.name());
        if (reportCategoryEnum.getPeriod() == AssociationReportPeriodCategoryEnum.MONTH) {
            return dataStore.storeFromSystemJobCheck(targetDate.getYear(), targetDate.getMonthValue());
        } else if (reportCategoryEnum.getPeriod() == AssociationReportPeriodCategoryEnum.QUARTER) {
            return dataStore.storeFromSystemJobCheck(targetDate.getYear(), DateUtil.ensureQuarter(targetDate.getMonthValue()));
        } else {
            throw new MithrasException("报表周期类型非月度或季度");
        }
    }

    private LocalDate ensureLastPeriodLastDay(LocalDate currentDate, AssociationReportPeriodCategoryEnum reportPeriodCategoryEnum) {
        if (reportPeriodCategoryEnum == AssociationReportPeriodCategoryEnum.QUARTER) {
            int quarter = DateUtil.ensureQuarter(currentDate.getMonthValue());
            if (quarter == 1) {
                // 取上一年第4季度最后一天
                return LocalDate.of(currentDate.getYear() - 1, 12, 31);
            } else {
                return DateUtil.ensureQuarterLastDay(currentDate.getYear(), quarter - 1);
            }
        } else if (reportPeriodCategoryEnum == AssociationReportPeriodCategoryEnum.MONTH) {
            if (currentDate.getMonthValue() == 1) {
                return LocalDate.of(currentDate.getYear() - 1, 12, 31);
            } else {
                return DateUtil.endOfMonth(currentDate.minusMonths(1));
            }
        } else {
            throw new MithrasException("报表周期类型非月度或季度");
        }
    }

    private void generateTodo(LocalDate jobExecDate) {
        // 评审会秘书待办
        try {
            this.generateSecretaryjuryMonthTodo(jobExecDate);
        } catch (Exception e) {
            log.error("金融局报送自动取值-评审会秘书待办生成发生异常", e);
        }
        // 财务经理待办需要判断是否是季度首月，如果是季度首月还需要推季报
        if (isQuarterFirstMonth(jobExecDate)) {
            try {
                this.generateFinancialmanagerQuarterTodo(jobExecDate);
            } catch (Exception e) {
                log.error("金融局报送自动取值-财务经理季度待办生成发生异常", e);
            }
        } else {
            try {
                this.generateFinancialmanagerMonthTodo(jobExecDate);
            } catch (Exception e) {
                log.error("金融局报送自动取值-财务经理月度待办生成发生异常", e);
            }
        }
    }

    private void generateSecretaryjuryMonthTodo(LocalDate jobExecDate) {
        // 判断是否存在待办，存在的话就不生成了
        if (this.isExistTodo(ProcessModelTypeEnum.AssociationReportMainBusinessFlow.name())) {
            log.info("金融局报送-自动取值-发送评审会秘书待办-存在待提交的待办，不执行逻辑");
            return;
        }
        // 评审会秘书待办
        List<Long> userIds = sysUserService.jobUsers(Collections.singleton(JobEnum.secretaryjury.name()));
        if (CollectionUtil.isEmpty(userIds)) {
            log.error("金融局报送-自动取值-发送评审会秘书待办-没有找到岗位为评审会秘书的用户");
            return;
        }
        LocalDate targetDate = this.ensureLastPeriodLastDay(jobExecDate, AssociationReportPeriodCategoryEnum.MONTH);
        // 查询目标月份的自动生成的待报送数据
        List<AssociationReport> list = this.queryAssociationReportList(targetDate.getYear(), targetDate.getMonthValue());
        Set<String> reportInstanceIds = new HashSet<>();
        for (AssociationReport associationReport : list) {
            if (StrUtil.equals(associationReport.getReportCategoryCode(), AssociationReportCategoryEnum.J0009.name())) {
                reportInstanceIds.add(associationReport.getReportInstanceId());
            }
        }
        if (CollectionUtil.isEmpty(reportInstanceIds)) {
            return;
        }
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 生成批次
                AssociationReportApply associationReportApply = new AssociationReportApply();
                associationReportApply.setReportInstanceIds(StrUtil.join(",", reportInstanceIds));
                associationReportApply.setApprovalStatus(AssociationProcessStatusEnum.UN_SUBMIT.name());
                associationReportApplyService.save(associationReportApply);
                // 生成待办
                CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder()
                        .processType(ProcessModelTypeEnum.AssociationReportMainBusinessFlow.name())
                        .businessId(associationReportApply.getId().toString())
                        .formName(String.format("%s年%s月金融局报送-评审会秘书待办", targetDate.getYear(), targetDate.getMonthValue()))
                        .currentNode("评审会秘书确认")
                        .currentAssignee(userIds.toString())
                        .applyTime(LocalDateTime.now())
                        .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                        .build();
                processPrepareService.save(commonProcessPrepare);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("金融局报送自动取值-评审会秘书待办数据保存异常", e);
            }
        });
    }

    private void generateFinancialmanagerMonthTodo(LocalDate jobExecDate) {
        // 判断是否存在待办，存在的话就不生成了
        if (this.isExistTodo(ProcessModelTypeEnum.AssociationReportQuarterMonthFlow.name())) {
            log.info("金融局报送-自动取值-发送财务经理月度待办-存在待提交的待办，不执行逻辑");
            return;
        }
        // 财务经理待办
        List<Long> userIds = sysUserService.jobUsers(Collections.singleton(JobEnum.financialmanager.name()));
        if (CollectionUtil.isEmpty(userIds)) {
            log.error("金融局报送-自动取值-发送财务经理月度待办-没有找到岗位为财务经理的用户");
            return;
        }
        LocalDate targetDate = this.ensureLastPeriodLastDay(jobExecDate, AssociationReportPeriodCategoryEnum.MONTH);
        // 查询目标月份的自动生成的待报送数据
        List<AssociationReport> list = this.queryAssociationReportList(targetDate.getYear(), targetDate.getMonthValue());
        Set<String> reportInstanceIds = new HashSet<>();
        for (AssociationReport associationReport : list) {
            if (StrUtil.equals(associationReport.getReportCategoryCode(), AssociationReportCategoryEnum.J0005.name())) {
                reportInstanceIds.add(associationReport.getReportInstanceId());
            }
        }
        if (CollectionUtil.isEmpty(reportInstanceIds)) {
            log.info("金融局报送-自动取值-发送财务经理月度待办-无目标报表数据");
            return;
        }
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 生成批次
                AssociationReportApply associationReportApply = new AssociationReportApply();
                associationReportApply.setReportInstanceIds(StrUtil.join(",", reportInstanceIds));
                associationReportApply.setApprovalStatus(AssociationProcessStatusEnum.UN_SUBMIT.name());
                associationReportApplyService.save(associationReportApply);
                // 生成待办
                CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder()
                        .processType(ProcessModelTypeEnum.AssociationReportQuarterMonthFlow.name())
                        .businessId(associationReportApply.getId().toString())
                        .formName(String.format("%s年%s月金融局报送-财务经理待办", targetDate.getYear(), targetDate.getMonthValue()))
                        .currentNode("财务经理确认")
                        .currentAssignee(userIds.toString())
                        .applyTime(LocalDateTime.now())
                        .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                        .build();
                processPrepareService.save(commonProcessPrepare);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("金融局报送自动取值-财务经理月度待办数据保存异常", e);
            }
        });
    }

    private void generateFinancialmanagerQuarterTodo(LocalDate jobExecDate) {
        // 判断是否存在待办，存在的话就不生成了
        if (this.isExistTodo(ProcessModelTypeEnum.AssociationReportQuarterMonthFlow.name())) {
            log.info("金融局报送-自动取值-发送财务经理季度待办-存在待提交的待办，不执行逻辑");
            return;
        }
        List<Long> userIds = sysUserService.jobUsers(Collections.singleton(JobEnum.financialmanager.name()));
        if (CollectionUtil.isEmpty(userIds)) {
            log.error("金融局报送-自动取值-发送财务经理季度待办-没有找到岗位为财务经理的用户");
            return;
        }
        LocalDate targetMonthDate = this.ensureLastPeriodLastDay(jobExecDate, AssociationReportPeriodCategoryEnum.MONTH);
        LocalDate targetQuarterDate = this.ensureLastPeriodLastDay(jobExecDate, AssociationReportPeriodCategoryEnum.QUARTER);
        Set<String> reportInstanceIds = new HashSet<>();
        // 月报
        List<AssociationReport> monthList = this.queryAssociationReportList(targetMonthDate.getYear(), targetMonthDate.getMonthValue());
        for (AssociationReport associationReport : monthList) {
            if (StrUtil.equals(associationReport.getReportCategoryCode(), AssociationReportCategoryEnum.J0005.name())) {
                reportInstanceIds.add(associationReport.getReportInstanceId());
            }
        }
        // 季报
        Set<AssociationReportCategoryEnum> categorySets = AssociationReportCategoryEnum.allQuarterCategory();
        List<AssociationReport> quarterList = this.queryAssociationReportList(targetQuarterDate.getYear(), DateUtil.ensureQuarter(targetQuarterDate.getMonthValue()));
        for (AssociationReport associationReport : quarterList) {
            AssociationReportCategoryEnum categoryEnum = AssociationReportCategoryEnum.findByName(associationReport.getReportCategoryCode());
            if (Objects.isNull(categoryEnum)) {
                continue;
            }
            if (categoryEnum.getPeriod() == AssociationReportPeriodCategoryEnum.QUARTER) {
                reportInstanceIds.add(associationReport.getReportInstanceId());
                categorySets.remove(categoryEnum);
            }
        }
        if (CollectionUtil.isEmpty(reportInstanceIds)) {
            log.info("金融局报送-自动取值-发送财务经理季度待办-无目标报表数据");
            return;
        }
        if (CollectionUtil.isNotEmpty(categorySets)) {
            log.info("金融局报送-自动取值-发送财务经理季度待办-数据不完整，缺少报表:{}", JSONUtil.toJsonStr(categorySets));
            return;
        }
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 生成批次
                AssociationReportApply associationReportApply = new AssociationReportApply();
                associationReportApply.setReportInstanceIds(StrUtil.join(",", reportInstanceIds));
                associationReportApply.setApprovalStatus(AssociationProcessStatusEnum.UN_SUBMIT.name());
                associationReportApplyService.save(associationReportApply);
                // 生成待办
                CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder()
                        .processType(ProcessModelTypeEnum.AssociationReportQuarterMonthFlow.name())
                        .businessId(associationReportApply.getId().toString())
                        .formName(String.format("%s年%s季度金融局报送-财务经理待办", targetQuarterDate.getYear(), DateUtil.ensureQuarter(targetQuarterDate.getMonthValue())))
                        .currentNode("财务经理确认")
                        .currentAssignee(userIds.toString())
                        .applyTime(LocalDateTime.now())
                        .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                        .build();
                processPrepareService.save(commonProcessPrepare);
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("金融局报送自动取值-财务经理月度待办数据保存异常", e);
            }
        });
    }

    private List<AssociationReport> queryAssociationReportList(int year, int period) {
        LambdaQueryWrapper<AssociationReport> query = Wrappers.lambdaQuery();
        query.eq(AssociationReport::getReportYear, year);
        query.eq(AssociationReport::getReportPeriod, period);
        query.eq(AssociationReport::getReportStatus, AssociationReportStatusEnum.WAIT.name());
        query.eq(AssociationReport::getIsShow, YesOrNoNumberEnum.NO.getCode());
        return associationReportJobService.list(query);
    }

    private boolean isQuarterFirstMonth(LocalDate localDate) {
        LocalDate quarterFirstDay = DateUtil.ensureQuarterFirstDay(localDate.getYear(), DateUtil.ensureQuarter(localDate.getMonthValue()));
        return quarterFirstDay.getMonthValue() == localDate.getMonthValue();
    }

    private boolean isExistTodo(String processType) {
        LambdaQueryWrapper<CommonProcessPrepare> query = Wrappers.lambdaQuery();
        query.eq(CommonProcessPrepare::getProcessType, processType);
        query.eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name());
        return processPrepareService.count(query) > 0;
    }
}
