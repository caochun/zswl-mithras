package cn.zswltech.mithras.application.orchestration.job.monthly;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.monthly.MonthlyCostREQ;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.monthly.application.job.FundsDailyCostJobService;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.monthly.mapper.model.FundsDailyCost;
import cn.zswltech.mithras.monthly.mapper.model.FundsDailyCostMain;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.application.orchestration.monthly.FundsDailyCostService;
import cn.zswltech.mithras.application.orchestration.monthly.MonthlyManageService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/11/5
 * @description
 */
@Slf4j
@Component
public class FundsDailyCostJobServiceImpl implements FundsDailyCostJobService {
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private FundsDailyCostMainService fundsDailyCostMainService;
    @Resource
    private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;

    @Override
    public void fundsDailyCostMainFinishJob(String param) {
        // 正常情况，定时任务设定在每个月的1号凌晨
        LocalDate date;
        if (StrUtil.isNotBlank(param)) {
            date = LocalDateTimeUtil.parseDate(param, DatePattern.NORM_DATE_PATTERN);
        } else {
            date = LocalDate.now();
        }
        // 取上个月的1号为比较日期
        LocalDate targetDate = LocalDate.of(date.getYear(), date.getMonthValue(), 1).minusMonths(1);
        List<FundFinancingBaseInfo> fundFinancingBaseInfoList = financingBaseInfoService.list(
                Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.SETTLE.name())
        );
        List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoService.list(
                Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                        .eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.SETTLE.name())
        );
        // 应付利息完结
        for (FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfoList) {
            try {
                // 如果结清日期在上个月1号之前，则结束计提利息（结清当月仍需进行计提）
                if (fundFinancingBaseInfo.getActualExpireDate().isBefore(targetDate)) {
                    fundsDailyCostMainService.finish(fundFinancingBaseInfo.getId(), false);
                }
            } catch (Exception e) {
                log.error("间融{}应付利息结束发生异常", fundFinancingBaseInfo.getFinancingCode(), e);
            }
        }
        for (FundDirectFinancingBaseInfo directFinancingBaseInfo : directFinancingBaseInfoList) {
            try {
                List<FundDirectFinancingRepayActual> repayActualList = fundDirectFinancingRepayActualService.listByFinancingId(directFinancingBaseInfo.getId());
                repayActualList.sort(Comparator.comparing(FundDirectFinancingRepayActual::getRepayDate).reversed());
                if (repayActualList.get(0).getRepayDate().isBefore(targetDate)) {
                    fundsDailyCostMainService.finish(directFinancingBaseInfo.getId(), true);
                }
            } catch (Exception e) {
                log.error("直融{}应付利息结束发生异常", directFinancingBaseInfo.getFinancingCode(), e);
            }
        }
    }

    @Override
    public void fundsDailyCostInit(String param) {
        String targetDateParam = "2026-01-31";
//        String targetDateParam = param;
        if (StrUtil.isBlank(targetDateParam)) {
            return;
        }
        LocalDate targetEndDate = LocalDateTimeUtil.parseDate(targetDateParam, DatePattern.NORM_DATE_PATTERN);
//        // 初始化主表
//        this.initMain();
        // 初始化明细表
        this.initDetail(targetEndDate);
    }

    private void initMain() {
        List<FundFinancingBaseInfo> indirectList = SpringUtil.getBean(FundFinancingBaseInfoService.class).list(
                Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .in(FundFinancingBaseInfo::getFinancingStatus, ListUtil.toList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()))
        );
        for (FundFinancingBaseInfo financingBaseInfo : indirectList) {
            // 非期初一次性收息
            boolean condition1 = Objects.equals(financingBaseInfo.getInitialInterestReceivedOnce(), YesOrNoNumberEnum.NO.getCode());
            // 非银票和商票
            boolean condition2 = !StrUtil.equalsAny(financingBaseInfo.getBusinessType(), FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name(), FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name());
            if (condition1 && condition2) {
                SpringUtil.getBean(FundsDailyCostMainService.class).createByIndirect(financingBaseInfo);
            }
        }
        List<FundDirectFinancingBaseInfo> directList = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).list(
                Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                        .in(FundDirectFinancingBaseInfo::getFinancingStatus, ListUtil.toList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()))
        );
        for (FundDirectFinancingBaseInfo directFinancingBaseInfo : directList) {
            SpringUtil.getBean(FundsDailyCostMainService.class).createByDirect(directFinancingBaseInfo);
        }
    }

    private void initDetail(LocalDate endDate) {
        List<FundsDailyCostMain> todoList = fundsDailyCostMainService.list();
        for (FundsDailyCostMain main : todoList) {
            MonthlyCostREQ req = new MonthlyCostREQ();
            req.setFinancingId(main.getFinancingId());
            req.setFinancingType(main.getFinancingType());
            req.setYearAndMonth(LocalDateTimeUtil.format(endDate, DatePattern.NORM_MONTH_PATTERN));
            SpringUtil.getBean(MonthlyManageService.class).calculateDailyInterest2(req);
//            // 按月计算保证计算逻辑不异常
//            LocalDate targetDate = LocalDate.of(main.getCarryInterestDate().getYear(), main.getCarryInterestDate().getMonthValue(), 1);
//            while (!targetDate.isAfter(endDate)) {
//                req.setYearAndMonth(LocalDateTimeUtil.format(targetDate, DatePattern.NORM_MONTH_PATTERN));
//                SpringUtil.getBean(MonthlyManageService.class).calculateDailyInterest2(req);
//                targetDate = targetDate.plusMonths(1);
//            }
        }
    }

    private void refreshMain() {
        List<FundsDailyCostMain> all = fundsDailyCostMainService.list();
        for (FundsDailyCostMain main : all) {
            List<FundsDailyCost> list = SpringUtil.getBean(FundsDailyCostService.class).list(
                    Wrappers.<FundsDailyCost>lambdaQuery()
                            .eq(FundsDailyCost::getFinancingId, main.getFinancingId())
                            .eq(FundsDailyCost::getType, main.getFinancingType())
                            .ne(FundsDailyCost::getItemText, FundsDailyCostService.BEGINNING_ITEM_TEXT)
                            .orderByAsc(FundsDailyCost::getInterestDate)
            );
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            FundsDailyCost lastOne = list.get(list.size() - 1);
            LocalDate startDate = LocalDate.of(lastOne.getInterestDate().getYear(), lastOne.getInterestDate().getMonthValue(), 1);
            LocalDate endDate = LocalDate.of(lastOne.getInterestDate().getYear(), lastOne.getInterestDate().getMonthValue(), lastOne.getInterestDate().lengthOfMonth());
            main.setLastUpdateDate(endDate);
            main.setTotalCapitalCostThisYear(lastOne.getTotalCapitalCost());
            main.setTotalCapitalCostThisMonth(list.stream().filter(e -> !e.getInterestDate().isBefore(startDate) && !e.getInterestDate().isAfter(endDate)).mapToLong(FundsDailyCost::getFinancingCost).sum());
        }
        fundsDailyCostMainService.updateBatchById(all);
    }
}
