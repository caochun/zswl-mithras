package cn.zswltech.mithras.application.orchestration.job.kpi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.kpi.application.port.KpiProjectBonusJobPort;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.finance.FinanceProjectProfitDetailService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjGuessService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/6/20
 * @description
 */
@Slf4j
@Component
public class KpiProjectBonusJobPortAdapter implements KpiProjectBonusJobPort {
    @Resource
    private FinanceProjectProfitDetailService financeProjectProfitDetailService;
    @Resource
    private KpiProjGuessService kpiProjGuessService;

    @Override
    public void calculateKpiProjectBonus(String param) {
        try {
            if (StrUtil.isNotBlank(param)) {
                this.executeByAdmin(param);
                return;
            }
            LocalDate now = LocalDate.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            if (month == 1) {
                year = year - 1;
                month = 12;
            } else {
                month = month - 1;
            }
            // 从项目利润详情中获取所有营收大于0的合同进行绩效测算
            List<FinanceProjectProfitDetail> detailList = financeProjectProfitDetailService.listHasIncomeContractDetail(year, month);
            if (CollectionUtil.isEmpty(detailList)) {
                log.error("绩效测算任务-没有找到需要进行测算的数据");
                return;
            }
            // 计算项目绩效奖金
            this.doCalculate(detailList, year, month);
        } catch (Exception e) {
            log.error("执行项目绩效奖金计算任务发生异常", e);
        }
    }

    private void executeByAdmin(String param) {
        // 手动调用特殊处理
        JSONObject jsonObject = JSONUtil.parseObj(param);
        Long contractId = jsonObject.getLong("contractId");
        LocalDate targetDate = LocalDateTimeUtil.parseDate(jsonObject.getStr("targetDate"), DatePattern.NORM_DATE_PATTERN);
        int year = targetDate.getYear();
        int month = targetDate.getMonthValue();
        if (Objects.nonNull(contractId)) {
            kpiProjGuessService.calculate(contractId, year, month);
        } else {
            List<FinanceProjectProfitDetail> detailList = financeProjectProfitDetailService.listHasIncomeContractDetail(year, month);
            if (CollectionUtil.isEmpty(detailList)) {
                return;
            }
            this.doCalculate(detailList, year, month);
        }
    }

    private void doCalculate(List<FinanceProjectProfitDetail> detailList, int year, int month) {
        for (FinanceProjectProfitDetail detail : detailList) {
            try {
                MDC.put(GlobalConstants.LOG_TRACE_ID, detail.getContractId() + "_" + year + "_" + month + "_" + System.currentTimeMillis());
                kpiProjGuessService.calculate(detail.getContractId(), year, month);
            } catch (Exception e) {
                log.error("计算项目绩效奖金发生异常[contractId: {}]", detail.getContractId(), e);
            } finally {
                MDC.clear();
            }
        }
    }
}
