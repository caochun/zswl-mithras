package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataExchangeRate;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataLpr;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataSpecialDate;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.basedata.BaseDataExchangeRateService;
import cn.zswltech.mithras.service.service.basedata.BaseDataLprService;
import cn.zswltech.mithras.service.service.basedata.BaseDataSpecialDateService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.service.service.process.prepare.handle.BaseDataExchangeRateCommitHandle;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/5
 * @description
 */
@Slf4j
@Component
public class BaseDataJob {
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private BaseDataLprService baseDataLprService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private BaseDataExchangeRateService baseDataExchangeRateService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    /**
     * 汇率设置待办任务
     */
    @Transactional(rollbackFor = Throwable.class)
    @XxlJob("baseDataExchangeRateTodoJob")
    public void generateExchangeRateTodo() {
        // 获取所有财务经理人员
        List<Long> userIds = sysUserService.jobUsers(Collections.singleton(JobEnum.financialmanager.name()));
        if (CollectionUtil.isEmpty(userIds)) {
            log.error("没有找到岗位为财务经理的用户，不执行汇率设置待办推送任务");
            return;
        }
        LocalDate targetDate;
        if (StrUtil.isBlank(XxlJobHelper.getJobParam())) {
            targetDate = LocalDate.now();
        } else {
            targetDate = LocalDateTimeUtil.parseDate(XxlJobHelper.getJobParam(), DatePattern.NORM_DATE_PATTERN);
        }
        // 查询是否有当月的美元汇率
        List<BaseDataExchangeRate> currentMonthUSDList = baseDataExchangeRateService.queryByYearMonthCurrency(targetDate.getYear(), targetDate.getMonthValue(), "USD");
        if (CollectionUtil.isEmpty(currentMonthUSDList)) {
            // 生成一条待补全的草稿数据
            BaseDataExchangeRate exchangeRate = new BaseDataExchangeRate();
            exchangeRate.setTargetYear(targetDate.getYear());
            exchangeRate.setTargetMonth(targetDate.getMonthValue());
            exchangeRate.setCurrency("USD");
            exchangeRate.setIsDraft(YesOrNoNumberEnum.YES.getCode());
            baseDataExchangeRateService.save(exchangeRate);
        }
        LocalDateTime applyTime = LocalDateTime.now();
        // 生成当月待办
        CommonProcessPrepare todo = CommonProcessPrepare.builder()
                .processType(BaseDataExchangeRateCommitHandle.PROCESS_TYPE)
                .formName(String.format("%s年%s月汇率设置", targetDate.getYear(), targetDate.getMonthValue()))
                .currentNode("财务确认")
                .currentAssignee(JSONUtil.toJsonStr(userIds))
                .applyTime(LocalDateTime.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.getDayOfMonth(), applyTime.getHour(), applyTime.getMinute(), applyTime.getSecond()))
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        commonProcessPrepareService.save(todo);
    }

    /**
     * 放假调休数据导入提醒
     *
     * @return 任务执行结果
     */
    @XxlJob("specialDataRemindJob")
    public ReturnT<String> specialDataRemindJob() {
        int currentYear = LocalDateTime.now().getYear();
        int nextYear = currentYear + 1;
        LambdaQueryWrapper<BaseDataSpecialDate> query = Wrappers.lambdaQuery();
        query.eq(BaseDataSpecialDate::getYear, nextYear);
        List<BaseDataSpecialDate> list = baseDataSpecialDateService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            // TODO 通知方式待接入
            log.error("{}年放假调休日数据为空，请及时补全相关数据", nextYear);
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 当月LPR数据导入提醒
     *
     * @return 任务执行结果
     */
    @XxlJob("lprRemindJob")
    public ReturnT<String> lprRemindJob() {
        Date now = new Date();
        int month = DateUtil.month(now) + 1;
        Date start = DateUtil.beginOfMonth(now);
        Date end = DateUtil.endOfMonth(now);
        LambdaQueryWrapper<BaseDataLpr> query = Wrappers.lambdaQuery();
        query.ge(BaseDataLpr::getLprDate, DateUtil.toLocalDateTime(start));
        query.le(BaseDataLpr::getLprDate, DateUtil.toLocalDateTime(end));
        List<BaseDataLpr> list = baseDataLprService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            List<Long> userIdList = sysUserService.queryJobUserIds(JobEnum.financialmanager.name());
            if (CollectionUtil.isNotEmpty(userIdList)) {
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setTo(userIdList);
                messageAddREQ.setPcurl("/baseData/lpr");
                messageAddREQ.setContent("基础数据设置-LPR");
                messageAddREQ.setFlowid(DateUtil.format(now, DatePattern.PURE_DATETIME_PATTERN));
                messageAddREQ.setRelation("请至【基础数据设置】维护<" + month + ">月LPR");
                messageAddREQ.setMessageType(MessageTypeEnum.BASE_DATA_LPR_REMIND.name());
                messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
            }
        }
        return ReturnT.SUCCESS;
    }
}
