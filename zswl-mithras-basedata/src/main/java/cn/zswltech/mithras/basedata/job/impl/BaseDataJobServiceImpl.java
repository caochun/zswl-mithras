package cn.zswltech.mithras.basedata.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.basedata.job.BaseDataJobMessagePort;
import cn.zswltech.mithras.basedata.job.BaseDataJobService;
import cn.zswltech.mithras.basedata.job.BaseDataJobTodoPort;
import cn.zswltech.mithras.basedata.job.BaseDataJobUserPort;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataExchangeRate;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataLpr;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataSpecialDate;
import cn.zswltech.mithras.basedata.service.BaseDataExchangeRateService;
import cn.zswltech.mithras.basedata.service.BaseDataLprService;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
@Service
public class BaseDataJobServiceImpl implements BaseDataJobService {

    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private BaseDataLprService baseDataLprService;
    @Resource
    private BaseDataExchangeRateService baseDataExchangeRateService;
    @Resource
    private BaseDataJobUserPort userPort;
    @Resource
    private BaseDataJobMessagePort messagePort;
    @Resource
    private BaseDataJobTodoPort todoPort;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void generateExchangeRateTodo(String jobParam) {
        List<Long> userIds = userPort.jobUsers(Collections.singleton(JobEnum.financialmanager.name()));
        if (CollectionUtil.isEmpty(userIds)) {
            log.error("没有找到岗位为财务经理的用户，不执行汇率设置待办推送任务");
            return;
        }
        LocalDate targetDate;
        if (StrUtil.isBlank(jobParam)) {
            targetDate = LocalDate.now();
        } else {
            targetDate = LocalDateTimeUtil.parseDate(jobParam, DatePattern.NORM_DATE_PATTERN);
        }
        List<BaseDataExchangeRate> currentMonthUSDList = baseDataExchangeRateService.queryByYearMonthCurrency(targetDate.getYear(), targetDate.getMonthValue(), "USD");
        if (CollectionUtil.isEmpty(currentMonthUSDList)) {
            BaseDataExchangeRate exchangeRate = new BaseDataExchangeRate();
            exchangeRate.setTargetYear(targetDate.getYear());
            exchangeRate.setTargetMonth(targetDate.getMonthValue());
            exchangeRate.setCurrency("USD");
            exchangeRate.setIsDraft(YesOrNoNumberEnum.YES.getCode());
            baseDataExchangeRateService.save(exchangeRate);
        }
        todoPort.createExchangeRateTodo(targetDate.getYear(), targetDate.getMonthValue(), targetDate.getDayOfMonth(), LocalDateTime.now(), userIds);
    }

    @Override
    public ReturnT<String> specialDataRemindJob() {
        int currentYear = LocalDateTime.now().getYear();
        int nextYear = currentYear + 1;
        LambdaQueryWrapper<BaseDataSpecialDate> query = Wrappers.lambdaQuery();
        query.eq(BaseDataSpecialDate::getYear, nextYear);
        List<BaseDataSpecialDate> list = baseDataSpecialDateService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            log.error("{}年放假调休日数据为空，请及时补全相关数据", nextYear);
        }
        return ReturnT.SUCCESS;
    }

    @Override
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
            List<Long> userIdList = userPort.queryJobUserIds(JobEnum.financialmanager.name());
            if (CollectionUtil.isNotEmpty(userIdList)) {
                messagePort.sendLprRemind(userIdList, DateUtil.format(now, DatePattern.PURE_DATETIME_PATTERN), month);
            }
        }
        return ReturnT.SUCCESS;
    }
}
