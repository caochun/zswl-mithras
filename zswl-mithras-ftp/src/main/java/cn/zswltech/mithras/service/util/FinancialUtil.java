package cn.zswltech.mithras.service.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.enums.InterestWayEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.ftp.oldftp.bo.FtpCalculateHelperBO;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowCalculateBO;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowIRRBO;
import cn.zswltech.mithras.projectprocess.service.bo.DailyDiscountRateCalcResultBO;
import cn.zswltech.mithras.projectprocess.service.bo.IncomeSharingCashFlowBO;
import cn.zswltech.mithras.service.service.bo.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.projectprocess.enums.projestablish.PayType.AFTERWARD;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static cn.zswltech.mithras.service.others.Util.mithrasLongDecimalTwo;

/**
 * @author dingqi
 * @date 2022/12/13
 * @description
 */
@Slf4j
public class FinancialUtil {
    public static BigDecimal calculateProfitWithoutExpense(BigDecimal profitBD, Integer expenseRate) {
        if (profitBD.longValue() >= 0) {
            // 利润大于等于0，扣费后利润 = 利润 * （1 - 费用比例）
            return profitBD.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(expenseRate).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP)));
        } else {
            // 利润大于等于0，扣费后利润 = 利润 * （1 + 费用比例）
            return profitBD.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(expenseRate).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP)));
        }
    }

    public static BigDecimal calculateAmountWithoutTax(long amount, BigDecimal taxRate) {
        return BigDecimal.valueOf(amount).divide(BigDecimal.ONE.add(taxRate), 20, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateAmountWithoutTax(BigDecimal amount, BigDecimal taxRate) {
        return amount.divide(BigDecimal.ONE.add(taxRate), 20, RoundingMode.HALF_UP);
    }

    public static BigDecimal ensureConsultingTaxRate() {
        return BigDecimal.valueOf(0.06);
    }

    public static BigDecimal ensureValueAddedTaxRate(String leaseType) {
        if (Objects.equals(leaseType, LeaseType.hui_zu.name())) {
            return BigDecimal.valueOf(0.06);
        }
        if (Objects.equals(leaseType, LeaseType.zhi_zu.name())) {
            return BigDecimal.valueOf(0.13);
        }
        if (Objects.equals(leaseType, LeaseType.jyx_zu.name())) {
            return BigDecimal.valueOf(0.13);
        }
        return null;
    }

    /**
     * 剩余本金法收入明细
     *
     * @param actualLeaseDate   实际起租日
     * @param cashFlowList      现金流
     * @param dailyInterestRate 日利率
     * @return 收入明细列表
     */
    public static List<IncomeSharingCashFlowBO> calculateIncomeSharingByRP(LocalDate actualLeaseDate, List<CashFlowBO> cashFlowList, BigDecimal dailyInterestRate) {
        // 用实际起租日拼第0期
        CashFlowBO zeroCashFlow = new CashFlowBO();
        zeroCashFlow.setCashFlowPhase(0);
        zeroCashFlow.setCashFlowDate(actualLeaseDate);
        cashFlowList.add(zeroCashFlow);
        // 开始分摊收益
        List<IncomeSharingCashFlowBO> result = new LinkedList<>();
        cashFlowList.sort(Comparator.comparing(CashFlowBO::getCashFlowDate));
        LocalDate lastOne = cashFlowList.get(cashFlowList.size() - 1).getCashFlowDate();
        LocalDate lastIncomeDate = LocalDate.of(lastOne.getYear(), lastOne.getMonthValue(), lastOne.lengthOfMonth());
        LocalDate targetDate = cashFlowList.get(0).getCashFlowDate();
        do {
            LocalDate incomeDate = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth());
            LocalDate startDate = null;
            // 从后往前找到第一个大于目标日期的现金流
            int phase = 0;
            for (int i = cashFlowList.size() - 1; i >= 0; i--) {
                CashFlowBO cashFlow = cashFlowList.get(i);
                if (!cashFlow.getCashFlowDate().isAfter(incomeDate)) {
                    startDate = cashFlow.getCashFlowDate();
                    phase = cashFlow.getCashFlowPhase();
                    break;
                }
            }
            if (Objects.isNull(startDate)) {
                throw new RuntimeException("存在无法确定计算开始日的数据");
            }
            final LocalDate compareDate = startDate;
            // 计算剩余本金
            long remainingPrincipal = cashFlowList.stream().filter(e -> e.getCashFlowDate().isAfter(compareDate)).filter(e -> Objects.nonNull(e.getPrincipal())).mapToLong(CashFlowBO::getPrincipal).sum();
            // 计算天数
            long days = LocalDateTimeUtil.between(startDate.atStartOfDay(), incomeDate.atStartOfDay(), ChronoUnit.DAYS) + 1;
            // 计算收入
            long income = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(remainingPrincipal).multiply(dailyInterestRate).multiply(BigDecimal.valueOf(days)).longValue());
            // 封装结果并放入返回结果列表
            IncomeSharingCashFlowBO incomeSharingCashFlowBO = new IncomeSharingCashFlowBO();
            incomeSharingCashFlowBO.setBeginOfTermBalance(remainingPrincipal);
            incomeSharingCashFlowBO.setEndOfTermBalance(remainingPrincipal);
            incomeSharingCashFlowBO.setCashFlowPhase(phase);
            incomeSharingCashFlowBO.setCashFlowDate(incomeDate);
            incomeSharingCashFlowBO.setIncome(income);
            result.add(incomeSharingCashFlowBO);
            targetDate = targetDate.plusMonths(1);
        } while (!targetDate.isAfter(lastIncomeDate));
        return result;
    }

    /**
     * 实际利率法收入明细
     *
     * @param zeroCashFlowList   投放款&第0期应收款
     * @param cashFlowList          还款计划1-N期现金流
     * @param lastPhaseAdjustAmount 最后一期需调整金额
     * @param dailyDiscountRate     日折现率
     * @param actualLeaseDate       实际起租日
     * @return 收入明细列表
     */
    public static List<IncomeSharingCashFlowBO> calculateIncomeSharingByAIR(List<CashFlowBO> zeroCashFlowList, List<CashFlowBO> cashFlowList, Long lastPhaseAdjustAmount, BigDecimal dailyDiscountRate, LocalDate actualLeaseDate) {
        if (CollectionUtil.isEmpty(cashFlowList)) {
            return Collections.emptyList();
        }
        if (Objects.isNull(actualLeaseDate)) {
            throw new MithrasException("实际起租日为空");
        }
        // 根据实际起租日之前和之后分开
        Map<LocalDate, List<CashFlowBO>> m = zeroCashFlowList.stream().filter(e -> Objects.nonNull(e.getCashFlowDate())).collect(Collectors.groupingBy(CashFlowBO::getCashFlowDate));
        // 确定第0行数据
        CashFlowBO beginOfTermCashFlow = new CashFlowBO();
        beginOfTermCashFlow.setCashFlowDate(actualLeaseDate);
        long temp = 0L;
        for (Map.Entry<LocalDate, List<CashFlowBO>> entry : m.entrySet()) {
            if (entry.getKey().isAfter(actualLeaseDate)) {
                continue;
            }
            temp += entry.getValue().stream().filter(e -> Objects.nonNull(e.getCashFlowAmount())).mapToLong(CashFlowBO::getCashFlowAmount).sum();
        }
        beginOfTermCashFlow.setCashFlowAmount(Math.abs(temp));
        // 转换队列
        LinkedList<CashFlowBO> copyCashFlowList = new LinkedList<>(cashFlowList);
        cashFlowList.sort(Comparator.comparing(CashFlowBO::getCashFlowDate));
        // 定义结果存放对象
        List<IncomeSharingCashFlowBO> result = new LinkedList<>();
        LocalDate endDate;
        if (cashFlowList.size() > 1) {
            endDate = copyCashFlowList.getLast().getCashFlowDate();
        } else {
            endDate = copyCashFlowList.getFirst().getCashFlowDate();
        }
        Long preEndOfTermBalance = beginOfTermCashFlow.getCashFlowAmount();
        int curPhase = 1;
        LocalDate curDate = beginOfTermCashFlow.getCashFlowDate();
        if (!cashFlowList.isEmpty()) {
            do {
                IncomeSharingCashFlowBO row = new IncomeSharingCashFlowBO();
                row.setCashFlowDate(curDate);
                row.setCashFlowPhase(curPhase);
                if (row.getCashFlowDate().isEqual(copyCashFlowList.getFirst().getCashFlowDate())) {
                    row.setRent(Optional.ofNullable(copyCashFlowList.getFirst().getRent()).orElse(0L));
                    copyCashFlowList.pop();
                    curPhase++;
                }
                row.setBeginOfTermBalance(preEndOfTermBalance);
                // 调整逻辑：期初余额 = 上一行的期末余额 + （实收日期为当天的净投放金额）
                if (CollectionUtil.isNotEmpty(m.get(curDate)) && curDate.isAfter(actualLeaseDate)) {
                   long addAmount = m.get(curDate).stream().filter(e -> Objects.nonNull(e.getCashFlowAmount())).mapToLong(CashFlowBO::getCashFlowAmount).sum();
                   row.setBeginOfTermBalance(row.getBeginOfTermBalance() + Math.abs(addAmount));
                }
                row.setIncome(Util.mithrasLongDecimalTwo(BigDecimal.valueOf(row.getBeginOfTermBalance()).multiply(dailyDiscountRate).longValue()));
                row.setEndOfTermBalance(row.getBeginOfTermBalance() - Optional.ofNullable(row.getRent()).orElse(0L) + row.getIncome());
                result.add(row);
                curDate = curDate.plus(1, ChronoUnit.DAYS);
                preEndOfTermBalance = row.getEndOfTermBalance();
            } while (curDate.isBefore(endDate));
        }
        // 最后一期确认收入 = 应收利息合计 + 前期已收入金额 - 前面所有收入合计
        CashFlowBO lastOneCashFlow = copyCashFlowList.pop();
        long interestSum = cashFlowList.stream().filter(e -> Objects.nonNull(e.getInterest())).mapToLong(CashFlowBO::getInterest).sum();
        long incomeSum = result.stream().filter(e -> Objects.nonNull(e.getIncome())).mapToLong(IncomeSharingCashFlowBO::getIncome).sum();
        Long lastOneIncome = interestSum + lastPhaseAdjustAmount - incomeSum;
        IncomeSharingCashFlowBO lastOneBO = new IncomeSharingCashFlowBO();
        lastOneBO.setCashFlowDate(lastOneCashFlow.getCashFlowDate());
        lastOneBO.setCashFlowPhase(lastOneCashFlow.getCashFlowPhase());
        lastOneBO.setBeginOfTermBalance(preEndOfTermBalance);
        lastOneBO.setRent(lastOneCashFlow.getRent());
        lastOneBO.setIncome(lastOneIncome);
        lastOneBO.setEndOfTermBalance(0L);
        result.add(lastOneBO);
        return result;
    }

    /**
     * 计算日折现率
     *
     * @param cashFlowList 还款计划0-N期现金流
     * @return 日折现率和折现明细
     */
    public static DailyDiscountRateCalcResultBO calculateDailyDiscountRate(List<CashFlowBO> cashFlowList) {
        if (CollectionUtil.isEmpty(cashFlowList)) {
            throw new MithrasException("现金流为空，无法计算日折现率");
        }
        cashFlowList.sort(Comparator.comparing(CashFlowBO::getCashFlowDate));
        // 按天构建实率现金流
        List<DailyDiscountRateCalcResultBO.CashFlowAdjustBO> cashFlowAdjustList = new LinkedList<>();
        LocalDate prevDate = cashFlowList.get(0).getCashFlowDate();
        for(CashFlowBO cashFlowBO : cashFlowList){
            if(cashFlowBO.getCashFlowPhase()!=null && cashFlowBO.getCashFlowPhase()==0){
                prevDate = cashFlowBO.getCashFlowDate();
                break;
            }
        }
        int prevDays = 0;
        for (CashFlowBO cashFlowBO : cashFlowList) {
            LocalDate cur = cashFlowBO.getCashFlowDate();
            long days;
            if (prevDate.isEqual(cur) || prevDate.isAfter(cur)) {
                days = 0;
            } else {
                days = LocalDateTimeUtil.between(prevDate.atStartOfDay(), cur.atStartOfDay(), ChronoUnit.DAYS);
            }
            DailyDiscountRateCalcResultBO.CashFlowAdjustBO cashFlowAdjust = new DailyDiscountRateCalcResultBO.CashFlowAdjustBO();
            cashFlowAdjust.setCashFlowAmount(cashFlowBO.getCashFlowAmount());
            cashFlowAdjust.setCurrentPhaseDays((int) days);
            cashFlowAdjust.setCashFlowPhase(cashFlowAdjust.getCurrentPhaseDays() + prevDays);
            cashFlowAdjustList.add(cashFlowAdjust);
            if(prevDate.isEqual(cur) || prevDate.isBefore(cur)) {
                prevDate = cur;
                prevDays = cashFlowAdjust.getCashFlowPhase();
            }
        }
        // 单变量求解日折现率
        // 二分法尝试找到能够使得现金流相加趋近于0的数值
        BigDecimal left = BigDecimal.valueOf(0.00);
        BigDecimal right = BigDecimal.valueOf(0.20);
        BigDecimal dailyDiscountRate = BigDecimal.valueOf(-1);
        int i = 0;
        int max = 1000;
        BigDecimal diff;
        while (i < max) {
            dailyDiscountRate = left.add(right).divide(BigDecimal.valueOf(2), 20, RoundingMode.HALF_UP);
            BigDecimal sum = BigDecimal.ZERO;
            for (DailyDiscountRateCalcResultBO.CashFlowAdjustBO cashFlowAdjust : cashFlowAdjustList) {
                // 计算折现后现金流的合计值 折现后的现金流 = 调整前的现金流 / (1 + 日折现率) ^ n 其中n = 按天构建的期次
                double d = Math.pow(dailyDiscountRate.add(BigDecimal.ONE).doubleValue(), cashFlowAdjust.getCashFlowPhase().doubleValue());
                BigDecimal adjustCashFlowAmount = BigDecimal.valueOf(cashFlowAdjust.getCashFlowAmount()).divide(BigDecimal.valueOf(d), 30, RoundingMode.HALF_UP);
                sum = sum.add(adjustCashFlowAmount);
                cashFlowAdjust.setAdjustCashFlowAmount(adjustCashFlowAmount);
            }
            // 和0相比得到差值
            diff = sum.subtract(BigDecimal.ZERO);
            // 如果0后面已经达到20位精度则停止计算
            if (diff.abs().compareTo(BigDecimal.valueOf(0.00000000000000000001)) < 0) {
                break;
            }
            // 如果合计值 > 0 则说明日折现率要调大，在右半区取值，反之则要调小日折现率，在左半区取值
            if (sum.compareTo(BigDecimal.ZERO) > 0) {
                left = dailyDiscountRate;
            } else if (sum.compareTo(BigDecimal.ZERO) < 0) {
                right = dailyDiscountRate;
            } else {
                break;
            }
            i++;
        }
        // 封装结果
        DailyDiscountRateCalcResultBO bo = new DailyDiscountRateCalcResultBO();
        bo.setDailyDiscountRate(dailyDiscountRate);
        bo.setCashFlowAdjustList(cashFlowAdjustList);
        return bo;
    }

    public static Integer calculateFeeRate(Long target, Long total) {
        if (total == 0) {
            // 兼容0
            total = 1L;
        }
        return BigDecimal.valueOf(target).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000)).intValue();
    }

    public static Integer calculateFtp(List<FtpCalculateHelperBO> factorList) {
        log.info("计算FTP因子:{}", JSONUtil.toJsonStr(factorList));
        if (CollectionUtil.isEmpty(factorList)) {
            return null;
        }
        boolean isWeightAverage = false;
        long totalNetAmount = 0L;
        for (FtpCalculateHelperBO ftpCalculateHelperBO : factorList) {
            totalNetAmount = Optional.ofNullable(ftpCalculateHelperBO.getPayAmount()).orElse(0L) - Optional.ofNullable(ftpCalculateHelperBO.getCollectAmount()).orElse(0L);
        }
        if (totalNetAmount > 0) {
            isWeightAverage = true;
        }
        if (isWeightAverage) {
            long total = factorList.stream().mapToLong(item -> item.getPayAmount() - item.getCollectAmount()).sum();
            // 加权平均
            BigDecimal b = BigDecimal.ZERO;
            for (FtpCalculateHelperBO ftpCalculateHelperBO : factorList) {
                if (Objects.nonNull(ftpCalculateHelperBO.getPayAmount()) && ftpCalculateHelperBO.getPayAmount() > 0) {
                    b = BigDecimal.valueOf(ftpCalculateHelperBO.getPayAmount() - ftpCalculateHelperBO.getCollectAmount()).divide(BigDecimal.valueOf(total), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(ftpCalculateHelperBO.getFtp())).add(b);
                }
            }
            // 保留两位小数后返回
            return Util.mithrasIntegerDecimalTwo(b.intValue());
        } else {
            // 算术平均
            return Util.mithrasIntegerDecimalTwo(factorList.stream().mapToInt(FtpCalculateHelperBO::getFtp).sum() / factorList.size());
        }
    }

    /**
     * 计算FTP日利率
     *
     * @param ftp FTP（带%）
     * @return FTP日利率（带%）
     */
    public static BigDecimal calculateFtpPerDay(BigDecimal ftp) {
        return ftp.divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP);
    }

    /**
     * 计算PMT（用于期末付款，期初不支持）
     *
     * @param rate         期利率（不带百分号）
     * @param times        还款期数
     * @param presentValue 现值（单位：毫厘）
     * @return 每期还款（单位：毫厘）
     */
    public static BigDecimal pmt(BigDecimal rate, Integer times, BigDecimal presentValue) {
        if (Objects.isNull(rate) || rate.doubleValue() <= 0) {
            throw new MithrasException("<利率/费率>必须大于0");
        }
        /*
         * 计算公式
         * (a * i * (1 + i) ^ n) / ((1 + i) ^ n - 1)
         * 其中 a = 贷款总额（现值），i = 期利率，n = 还款期数
         */
        BigDecimal b = (presentValue.multiply(rate).multiply(rate.add(BigDecimal.ONE).pow(times))).divide((rate.add(BigDecimal.ONE).pow(times)).subtract(BigDecimal.ONE), 0, RoundingMode.HALF_UP);
        // 兼容系统金额字段逻辑，厘和毫厘位重置为0
        Long result = mithrasLongDecimalTwo(b.longValue());
        return BigDecimal.valueOf(result);
    }

    /**
     * 生成现金流量表
     */
    public static List<CashFlowBO> calcCashFlow(CashFlowCalculateBO cashFlowCalculateBO) {
        RepayCalcType repayCalcType = RepayCalcType.find(cashFlowCalculateBO.getRentalCalcType());
        if (Objects.isNull(repayCalcType)) {
            throw new MithrasException("未定义的还款计算方式");
        }
        if (Objects.equals(cashFlowCalculateBO.getInterestWay(), InterestWayEnum.FLAT_RATE.name())) {
            return generateByPXF(cashFlowCalculateBO);
        } else {
            switch (repayCalcType) {
                case DEBX: {
                    return generateByDEBX(cashFlowCalculateBO);
                }
                case DEBJ: {
                    return generateByDEBJ(cashFlowCalculateBO);
                }
                case QTDQHB: {
                    return generateByQTDQHB(cashFlowCalculateBO);
                }
                case DQYCHBFX: {
                    return generateByDQYCHBFX(cashFlowCalculateBO);
                }
                default: {
                    throw new MithrasException("当前类型不支持");
                }
            }
        }
    }

    private static LocalDate ensureFirstCashFlowDate(LocalDate startDate, String payType, int monthAdd) {
        // 后付，第一期的日期为计划起租日加上还款周期
        if (AFTERWARD.name().equals(payType)) {
            return startDate.atStartOfDay().plusMonths(monthAdd).toLocalDate();
        } else {
            return startDate.atStartOfDay().toLocalDate();
        }
    }

    private static CashFlowBO calcZeroCashFlow(CashFlowCalculateBO cashFlowCalculateBO) {
        CashFlowBO zeroCashFlow = new CashFlowBO();
        zeroCashFlow.setCashFlowDate(cashFlowCalculateBO.getStartDate());
        zeroCashFlow.setCashFlowPhase(0);
        /// 现金流 = -授信金额 + 首期租金 + 保证金 + 服务费 + 手续费 + 首期利息
        long cf0 = cashFlowCalculateBO.getCreditAmount() * -1
                + LongUtil.null2zero(cashFlowCalculateBO.getDownPayment())
                + LongUtil.null2zero(cashFlowCalculateBO.getEarnestMoney())
                + LongUtil.null2zero(cashFlowCalculateBO.getConsultingFee())
                + LongUtil.null2zero(cashFlowCalculateBO.getCommission())
                + LongUtil.null2zero(cashFlowCalculateBO.getFirstInstallmentInterest());
        zeroCashFlow.setCashFlowAmount(cf0);
        zeroCashFlow.setInterest(cashFlowCalculateBO.getFirstInstallmentInterest());
        zeroCashFlow.setRemainingPrincipal(cashFlowCalculateBO.getCreditAmount() - LongUtil.null2zero(cashFlowCalculateBO.getDownPayment()));
        return zeroCashFlow;
    }

    private static CashFlowBO calcLastCashFlow(CashFlowBO preCashFlow, CashFlowCalculateBO cashFlowCalculateBO, BigDecimal rate, long currentRemainingPrinciple, int monthAdd) {
        CashFlowBO lastCashFlow = new CashFlowBO();
        lastCashFlow.setCashFlowDate(preCashFlow.getCashFlowDate().plusMonths(monthAdd).minusDays(1));
        lastCashFlow.setCashFlowPhase(cashFlowCalculateBO.getRepayTimes());
        // 最后一期的本金 = 上一期的剩余本金
        lastCashFlow.setPrincipal(currentRemainingPrinciple);
        BigDecimal interest = BigDecimal.valueOf(currentRemainingPrinciple).multiply(rate).setScale(0, RoundingMode.HALF_UP);
        lastCashFlow.setInterest(mithrasLongDecimalTwo(interest.longValue()));
        // 最后一期的租金 = 最后一期的本金 + 最后一期的利息
        lastCashFlow.setRent(lastCashFlow.getPrincipal() + lastCashFlow.getInterest());
        lastCashFlow.setRemainingPrincipal(0L);
        // 最后一期的现金流 = 最后一期租金 + 名义价款 - 保证金
        lastCashFlow.setCashFlowAmount(lastCashFlow.getRent() + LongUtil.null2zero(cashFlowCalculateBO.getNominalPrice()) - cashFlowCalculateBO.getEarnestMoney());
        return lastCashFlow;
    }

    private static List<CashFlowBO> generateByDQYCHBFX(CashFlowCalculateBO cashFlowCalculateBO) {
//        RepayRateEnum repayRateEnum = RepayRateEnum.of(cashFlowCalculateBO.getRepayRate());
//        err(Objects.isNull(repayRateEnum), "未定义的还款频率类型");
//        int monthAdd = getRepayIntervalMonth(repayRateEnum);
        // 到期一次性还本付息，忽略还款频率，还款期数固定1期
        cashFlowCalculateBO.setRepayTimes(1);
        // 期利率
//        BigDecimal rate = transformRate(cashFlowCalculateBO.getInterestRate(), repayRateEnum);
        // 年利率转化
        BigDecimal rate = BigDecimal.valueOf(cashFlowCalculateBO.getInterestRate())
                // 转成实际小数
                .divide(BigDecimal.valueOf(10000 * 100), 20, RoundingMode.HALF_UP)
                // 乘以多少年
                .multiply(BigDecimal.valueOf(cashFlowCalculateBO.getTotalMonth()).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP));
        int monthAdd = cashFlowCalculateBO.getTotalMonth();
        // 现金流
        List<CashFlowBO> result = new LinkedList<>();
        // 处理第0期
        CashFlowBO zeroCashFlow = calcZeroCashFlow(cashFlowCalculateBO);
        result.add(zeroCashFlow);
        // 到期一次性还本付息没有中间期项
        // 计算补全最后一期
        CashFlowBO lastCashFlow = calcLastCashFlow(result.get(result.size() - 1), cashFlowCalculateBO, rate, zeroCashFlow.getRemainingPrincipal(), monthAdd);
        result.add(lastCashFlow);
        return result;
    }

    private static List<CashFlowBO> generateByQTDQHB(CashFlowCalculateBO cashFlowCalculateBO) {
        RepayRateEnum repayRateEnum = RepayRateEnum.of(cashFlowCalculateBO.getRepayRate());
        err(Objects.isNull(repayRateEnum), "未定义的还款频率类型");
        int monthAdd = getRepayIntervalMonth(repayRateEnum);
        // 期利率
        BigDecimal rate = transformRate(cashFlowCalculateBO.getInterestRate(), repayRateEnum);
        // 现金流
        List<CashFlowBO> result = new LinkedList<>();
        // 处理第0期
        CashFlowBO zeroCashFlow = calcZeroCashFlow(cashFlowCalculateBO);
        result.add(zeroCashFlow);
        // 计算第1期到第n-1期
        LocalDate firstCashFlowDate = ensureFirstCashFlowDate(cashFlowCalculateBO.getStartDate(), cashFlowCalculateBO.getPayType(), monthAdd);
        long currentRemainingPrinciple = zeroCashFlow.getRemainingPrincipal();
        for (int i = 0; i < cashFlowCalculateBO.getRepayTimes() - 1; i++) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            // 还款日期
            cashFlowBO.setCashFlowDate(firstCashFlowDate.plusMonths((long) i * monthAdd));
            // 期项
            cashFlowBO.setCashFlowPhase(i + 1);
            // 利息 = 上一期的剩余本金 * 期利率
            BigDecimal interest = BigDecimal.valueOf(currentRemainingPrinciple).multiply(rate).setScale(0, RoundingMode.HALF_UP);
            cashFlowBO.setInterest(mithrasLongDecimalTwo(interest.longValue()));
            // 中间期项本金为0
            cashFlowBO.setPrincipal(0L);
            // 现金流
            cashFlowBO.setCashFlowAmount(cashFlowBO.getPrincipal() + cashFlowBO.getInterest());
            // 租金 = 现金流（除第0期和最后一期）
            cashFlowBO.setRent(cashFlowBO.getCashFlowAmount());
            // 剩余本金 = 上一期的剩余本金 - 本期本金
            currentRemainingPrinciple = currentRemainingPrinciple - cashFlowBO.getPrincipal();
            cashFlowBO.setRemainingPrincipal(currentRemainingPrinciple);
            result.add(cashFlowBO);
        }
        // 计算补全最后一期
        CashFlowBO lastCashFlow = calcLastCashFlow(result.get(result.size() - 1), cashFlowCalculateBO, rate, currentRemainingPrinciple, monthAdd);
        result.add(lastCashFlow);
        return result;
    }

    private static List<CashFlowBO> generateByDEBJ(CashFlowCalculateBO cashFlowCalculateBO) {
        RepayRateEnum repayRateEnum = RepayRateEnum.of(cashFlowCalculateBO.getRepayRate());
        err(Objects.isNull(repayRateEnum), "未定义的还款频率类型");
        int monthAdd = getRepayIntervalMonth(repayRateEnum);
        List<CashFlowBO> result = new LinkedList<>();
        // 处理第0期
        CashFlowBO zeroCashFlow = calcZeroCashFlow(cashFlowCalculateBO);
        result.add(zeroCashFlow);
        // 总剩余本金
        BigDecimal totalAmount = BigDecimal.valueOf(zeroCashFlow.getRemainingPrincipal());
        // 期利率
        BigDecimal rate = transformRate(cashFlowCalculateBO.getInterestRate(), repayRateEnum);
        BigDecimal principal = new BigDecimal(
                mithrasLongDecimalTwo(
                        totalAmount.divide(
                                new BigDecimal(cashFlowCalculateBO.getRepayTimes()), 0, RoundingMode.HALF_UP)
                                .longValue()
                ));
        // 计算第1期到第n-1期
        LocalDate firstCashFlowDate = ensureFirstCashFlowDate(cashFlowCalculateBO.getStartDate(), cashFlowCalculateBO.getPayType(), monthAdd);
        long currentRemainingPrinciple = zeroCashFlow.getRemainingPrincipal();
        for (int i = 0; i < cashFlowCalculateBO.getRepayTimes() - 1; i++) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            // 还款日期
            cashFlowBO.setCashFlowDate(firstCashFlowDate.plusMonths((long) i * monthAdd));
            // 期项
            cashFlowBO.setCashFlowPhase(i + 1);
            // 利息 = 上一期的剩余本金 * 期利率
            BigDecimal interest = BigDecimal.valueOf(currentRemainingPrinciple).multiply(rate).setScale(0, RoundingMode.HALF_UP);
            cashFlowBO.setInterest(mithrasLongDecimalTwo(interest.longValue()));
            // 本金固定
            cashFlowBO.setPrincipal(principal.longValue());
            // 现金流
            cashFlowBO.setCashFlowAmount(cashFlowBO.getPrincipal() + cashFlowBO.getInterest());
            // 租金 = 现金流（除第0期和最后一期）
            cashFlowBO.setRent(cashFlowBO.getCashFlowAmount());
            // 剩余本金 = 上一期的剩余本金 - 本期本金
            currentRemainingPrinciple = currentRemainingPrinciple - cashFlowBO.getPrincipal();
            cashFlowBO.setRemainingPrincipal(currentRemainingPrinciple);
            result.add(cashFlowBO);
        }
        // 计算补全最后一期
        CashFlowBO lastCashFlow = calcLastCashFlow(result.get(result.size() - 1), cashFlowCalculateBO, rate, currentRemainingPrinciple, monthAdd);
        result.add(lastCashFlow);
        return result;
    }

    private static List<CashFlowBO> generateByDEBX(CashFlowCalculateBO cashFlowCalculateBO) {
        RepayRateEnum repayRateEnum = RepayRateEnum.of(cashFlowCalculateBO.getRepayRate());
        err(Objects.isNull(repayRateEnum), "未定义的还款频率类型");
        int monthAdd = getRepayIntervalMonth(repayRateEnum);
        List<CashFlowBO> result = new LinkedList<>();
        // 处理第0期
        CashFlowBO zeroCashFlow = calcZeroCashFlow(cashFlowCalculateBO);
        result.add(zeroCashFlow);
        // 总剩余本金
        BigDecimal totalAmount = BigDecimal.valueOf(zeroCashFlow.getRemainingPrincipal());
        // 期利率
        BigDecimal rate = transformRate(cashFlowCalculateBO.getInterestRate(), repayRateEnum);
        // 计算每期还款
        BigDecimal rent = FinancialUtil.pmt(rate, cashFlowCalculateBO.getRepayTimes(), totalAmount);
        // 计算第1期到第n-1期
        LocalDate firstCashFlowDate = ensureFirstCashFlowDate(cashFlowCalculateBO.getStartDate(), cashFlowCalculateBO.getPayType(), monthAdd);
        long currentRemainingPrinciple = zeroCashFlow.getRemainingPrincipal();
        for (int i = 0; i < cashFlowCalculateBO.getRepayTimes() - 1; i++) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            // 还款日期
            cashFlowBO.setCashFlowDate(firstCashFlowDate.plusMonths((long) i * monthAdd));
            // 期项
            cashFlowBO.setCashFlowPhase(i + 1);
            // 现金流
            cashFlowBO.setCashFlowAmount(rent.longValue());
            // 租金 = 现金流（除第0期和最后一期）
            cashFlowBO.setRent(cashFlowBO.getCashFlowAmount());
            // 利息 = 上一期的剩余本金 * 期利率
            BigDecimal interest = BigDecimal.valueOf(currentRemainingPrinciple).multiply(rate).setScale(0, RoundingMode.HALF_UP);
            cashFlowBO.setInterest(mithrasLongDecimalTwo(interest.longValue()));
            // 本金 = 租金 - 利息
            cashFlowBO.setPrincipal(cashFlowBO.getRent() - cashFlowBO.getInterest());
            // 剩余本金 = 上一期的剩余本金 - 本期本金
            currentRemainingPrinciple = currentRemainingPrinciple - cashFlowBO.getPrincipal();
            cashFlowBO.setRemainingPrincipal(currentRemainingPrinciple);
            result.add(cashFlowBO);
        }
        // 计算补全最后一期
        CashFlowBO lastCashFlow = calcLastCashFlow(result.get(result.size() - 1), cashFlowCalculateBO, rate, currentRemainingPrinciple, monthAdd);
        result.add(lastCashFlow);
        return result;
    }

    private static List<CashFlowBO> generateByPXF(CashFlowCalculateBO cashFlowCalculateBO) {
        // 平息法
        // 计算每期还款
        // round((|CF0|*(1+r*n)/n),2)
        RepayRateEnum repayRateEnum = RepayRateEnum.of(cashFlowCalculateBO.getRepayRate());
        err(Objects.isNull(repayRateEnum), "未定义的还款频率类型");
        int monthAdd = getRepayIntervalMonth(repayRateEnum);
        List<CashFlowBO> result = new LinkedList<>();
        // 处理第0期
        CashFlowBO zeroCashFlow = calcZeroCashFlow(cashFlowCalculateBO);
        result.add(zeroCashFlow);
        // 总剩余本金
        BigDecimal totalAmount = BigDecimal.valueOf(zeroCashFlow.getRemainingPrincipal());
        //期利率
        BigDecimal rate = transformRate(cashFlowCalculateBO.getInterestRate(), repayRateEnum);
        BigDecimal rent = totalAmount.multiply( // |CF0|*
                BigDecimal.ONE.add( //1+
                        rate.multiply(new BigDecimal(cashFlowCalculateBO.getRepayTimes()))//r*n
                ))
                // /n
                .divide(new BigDecimal(cashFlowCalculateBO.getRepayTimes()), 10, RoundingMode.HALF_UP);
        // 本金均摊
        BigDecimal principal = totalAmount.divide(BigDecimal.valueOf(cashFlowCalculateBO.getRepayTimes()), 10, RoundingMode.HALF_UP);
        // 每期利息 = 每期租金 - 每期本金
        BigDecimal interest = rent.subtract(principal);
        // 计算第1期到第n-1期
        LocalDate firstCashFlowDate = ensureFirstCashFlowDate(cashFlowCalculateBO.getStartDate(), cashFlowCalculateBO.getPayType(), monthAdd);
        long currentRemainingPrinciple = zeroCashFlow.getRemainingPrincipal();
        for (int i = 0; i < cashFlowCalculateBO.getRepayTimes() - 1; i++) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            // 还款日期
            cashFlowBO.setCashFlowDate(firstCashFlowDate.plusMonths((long) i * monthAdd));
            // 期项
            cashFlowBO.setCashFlowPhase(i + 1);
            // 现金流
            cashFlowBO.setCashFlowAmount(rent.longValue());
            // 租金 = 现金流（除第0期和最后一期）
            cashFlowBO.setRent(Util.mithrasLongDecimalTwo(rent.longValue()));
            // 利息
            cashFlowBO.setInterest(Util.mithrasLongDecimalTwo(interest.longValue()));
            // 本金 = 租金 - 利息
            cashFlowBO.setPrincipal(cashFlowBO.getRent() - cashFlowBO.getInterest());
            // 剩余本金 = 上一期的剩余本金 - 本期本金
            currentRemainingPrinciple = currentRemainingPrinciple - cashFlowBO.getPrincipal();
            cashFlowBO.setRemainingPrincipal(currentRemainingPrinciple);
            result.add(cashFlowBO);
        }
        // 计算补全最后一期
        CashFlowBO lastCashFlow = calcLastCashFlow(result.get(result.size() - 1), cashFlowCalculateBO, rate, currentRemainingPrinciple, monthAdd);
        result.add(lastCashFlow);
        return result;
    }


    /**
     * 生成现金流量表
     *
     * @param cashFlowCalculateBO 生成现金流量表所需要的参数
     * @return 现金流量表计划
     */
    @Deprecated
    public static List<CashFlowBO> calculateCashFlow_deprecated(CashFlowCalculateBO cashFlowCalculateBO) {
        // 获取还款间隔月数
        RepayRateEnum repayRateEnum = RepayRateEnum.of(cashFlowCalculateBO.getRepayRate());
        if (Objects.isNull(repayRateEnum)) {
            throw new MithrasException("未定义的还款频率类型");
        }
        int monthAdd = getRepayIntervalMonth(repayRateEnum);
        List<CashFlowBO> result = new ArrayList<>(cashFlowCalculateBO.getRepayTimes());
        // 计算每期还款
        BigDecimal totalAmount = BigDecimal.valueOf(cashFlowCalculateBO.getCreditAmount() - cashFlowCalculateBO.getDownPayment());
        BigDecimal rate = transformRate(cashFlowCalculateBO.getInterestRate(), repayRateEnum);
        BigDecimal rent = FinancialUtil.pmt(rate, cashFlowCalculateBO.getRepayTimes(), totalAmount);
        // 处理第0期
        CashFlowBO firstCashFlow = new CashFlowBO();
        firstCashFlow.setCashFlowDate(cashFlowCalculateBO.getStartDate());
        firstCashFlow.setCashFlowPhase(0);
        // 现金流 = -授信金额 + 首期租金 + 保证金 + 服务费
        long cf0 = cashFlowCalculateBO.getCreditAmount() * -1 + cashFlowCalculateBO.getDownPayment();
        firstCashFlow.setCashFlowAmount(cf0 + cashFlowCalculateBO.getEarnestMoney() + cashFlowCalculateBO.getConsultingFee());
        firstCashFlow.setRemainingPrincipal(Math.abs(cf0));
        result.add(firstCashFlow);
        // 计算第1期到第n-1期
        LocalDate currentCashFlowData = cashFlowCalculateBO.getStartDate();
        long currentRemainingPrinciple = firstCashFlow.getRemainingPrincipal();
        for (int i = 0; i < cashFlowCalculateBO.getRepayTimes() - 1; i++) {
            CashFlowBO cashFlowBO = new CashFlowBO();
            // 还款日期
            currentCashFlowData = LocalDateTimeUtil.offset(currentCashFlowData.atStartOfDay(), monthAdd, ChronoUnit.MONTHS).toLocalDate();
            cashFlowBO.setCashFlowDate(currentCashFlowData);
            // 期项
            cashFlowBO.setCashFlowPhase(i + 1);
            // 现金流
            cashFlowBO.setCashFlowAmount(rent.longValue());
            // 租金 = 现金流（除第0期和最后一期）
            cashFlowBO.setRent(cashFlowBO.getCashFlowAmount());
            // 利息 = 上一期的剩余本金 * 期利率
            BigDecimal interest = BigDecimal.valueOf(currentRemainingPrinciple).multiply(rate).setScale(0, RoundingMode.HALF_UP);
            cashFlowBO.setInterest(mithrasLongDecimalTwo(interest.longValue()));
            // 本金 = 租金 - 利息
            cashFlowBO.setPrincipal(cashFlowBO.getRent() - cashFlowBO.getInterest());
            // 剩余本金 = 上一期的剩余本金 - 本期本金
            currentRemainingPrinciple = currentRemainingPrinciple - cashFlowBO.getPrincipal();
            cashFlowBO.setRemainingPrincipal(currentRemainingPrinciple);
            result.add(cashFlowBO);
        }
        // 计算补全最后一期
        CashFlowBO lastCashFlow = new CashFlowBO();
        lastCashFlow.setCashFlowDate(LocalDateTimeUtil.offset(currentCashFlowData.atStartOfDay(), monthAdd, ChronoUnit.MONTHS).toLocalDate());
        lastCashFlow.setCashFlowPhase(cashFlowCalculateBO.getRepayTimes());
        // 最后一期的本金 = 上一期的剩余本金
        lastCashFlow.setPrincipal(currentRemainingPrinciple);
        BigDecimal interest = BigDecimal.valueOf(currentRemainingPrinciple).multiply(rate).setScale(0, RoundingMode.HALF_UP);
        lastCashFlow.setInterest(mithrasLongDecimalTwo(interest.longValue()));
        // 最后一期的租金 = 最后一期的本金 + 最后一期的利息
        lastCashFlow.setRent(lastCashFlow.getPrincipal() + lastCashFlow.getInterest());
        lastCashFlow.setRemainingPrincipal(currentRemainingPrinciple - lastCashFlow.getPrincipal());
        // 最后一期的现金流 = 最后一期租金 + 名义价款 - 保证金
        lastCashFlow.setCashFlowAmount(lastCashFlow.getRent() + cashFlowCalculateBO.getNominalPrice() - cashFlowCalculateBO.getEarnestMoney());
        result.add(lastCashFlow);
        return result;
    }

    public static CashFlowIRRBO calculateIRR(Integer monthCount, RepayRateEnum repayRate, List<CashFlowBO> cashFlowList) {
        if (CollectionUtil.isEmpty(cashFlowList)) {
            throw new MithrasException("现金流为空，无法计算IRR");
        }
        if (Objects.isNull(monthCount)) {
            throw new MithrasException("租赁期限/额度有效期为空，无法计算IRR");
        }
        RepayRateEnum rr = repayRate;
        if (!RepayRateEnum.isByRule(repayRate.name())) {
            // 不规则默认按月
            rr = RepayRateEnum.MONTH;
        }
        List<CashFlowIRRBO.CashFlowAdjustBO> cashFlowAdjustList = adjustPhase(rr, cashFlowList);
        // 单变量求解
        return calculateIRRByAdjustList(cashFlowAdjustList, rr);
    }

    public static CashFlowIRRBO calculateIRRByAdjustList(List<CashFlowIRRBO.CashFlowAdjustBO> cashFlowAdjustList, RepayRateEnum repayRate) {
        int repayTimes;
        boolean isRule = RepayRateEnum.isByRule(repayRate.name());
        if (isRule) {
            repayTimes = getRepayTimesInYear(repayRate);
        } else {
            repayTimes = 12;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("计算IRR任务");
        // 二分法尝试找到能够使得现金流相加趋近于0的irr数值
        BigDecimal left = BigDecimal.valueOf(0.00).divide(BigDecimal.valueOf(repayTimes), 15, RoundingMode.HALF_UP);
        BigDecimal right = BigDecimal.valueOf(0.20).divide(BigDecimal.valueOf(repayTimes), 15, RoundingMode.HALF_UP);
        BigDecimal irr = BigDecimal.valueOf(-1);
        int i = 0;
        int max = 1000;
        BigDecimal diff;
        while (i < max) {
            irr = left.add(right).divide(BigDecimal.valueOf(2), 15, RoundingMode.HALF_UP);
            BigDecimal sum = BigDecimal.ZERO;
            for (CashFlowIRRBO.CashFlowAdjustBO cashFlowAdjustBO : cashFlowAdjustList) {
                // 计算折现后现金流的合计值 折现后的现金流 = 调整前的现金流 / (1 + irr) ^ n 其中n = 调整后的期项
                double d = Math.pow(irr.add(BigDecimal.ONE).doubleValue(), cashFlowAdjustBO.getAdjustCashFlowPhase().doubleValue());
                BigDecimal adjustCashFlowAmount = BigDecimal.valueOf(cashFlowAdjustBO.getCashFlowAmount()).divide(BigDecimal.valueOf(d), 30, RoundingMode.HALF_UP);
                sum = sum.add(adjustCashFlowAmount);
                cashFlowAdjustBO.setAdjustCashFlowAmount(adjustCashFlowAmount);
            }
            // 和0相比得到差值
            diff = sum.subtract(BigDecimal.ZERO);
            // 如果0后面已经达到15位精度则停止计算
            if (diff.abs().compareTo(BigDecimal.valueOf(0.000000000000001)) < 0) {
                break;
            }
            // 如果合计值 > 0 则说明irr要调大，在右半区取值，反之则要调小irr，在左半区取值
            if (sum.compareTo(BigDecimal.ZERO) > 0) {
                left = irr;
            } else if (sum.compareTo(BigDecimal.ZERO) < 0) {
                right = irr;
            } else {
                break;
            }
            i++;
        }
        stopWatch.stop();
        log.info("计算IRR得到结果 = {}， 尝试计算次数 = {}, 耗时 = {}ms", irr, i, stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        // 封装返回参数
        CashFlowIRRBO cashFlowIRRBO = new CashFlowIRRBO();
        cashFlowIRRBO.setIrrPerPhase(irr);
        if (isRule) {
            cashFlowIRRBO.setIrr(toYearIRR(cashFlowIRRBO.getIrrPerPhase(), repayRate));
        } else {
            cashFlowIRRBO.setIrr(toYearIRR(cashFlowIRRBO.getIrrPerPhase(), RepayRateEnum.MONTH));
        }
        cashFlowIRRBO.setCashFlowAdjustList(cashFlowAdjustList);
        return cashFlowIRRBO;
    }

    /**
     * 获取第0期还款日
     * 根据当前月份还款日、上一个月、下一个月，折中取李实际付款日最近的作为 第0期的还款日
     * @param snapshot 是否根据实际支付日期取当前年、月、日， false取系统当前年月
     * @param defaultCollectionDay 默认还款日
     */
    public static LocalDate ensureStandardZeroPhaseDate(Integer defaultCollectionDay, LocalDate actualPayDate, boolean snapshot) {
        LocalDate keyDate;
        if (snapshot) {
            keyDate = actualPayDate;
        } else {
            keyDate = LocalDate.now();
        }
        //这里假如是2月30日就异常了！！！待处理
        LocalDate standardDateThisMonth = LocalDate.of(keyDate.getYear(), keyDate.getMonthValue(), defaultCollectionDay);
        LocalDate standardDateLastMonth = standardDateThisMonth.plusMonths(-1);
        LocalDate standardDateNextMonth = standardDateThisMonth.plusMonths(1);
        List<Pair<Long, LocalDate>> candidateList = new ArrayList<>(3);
        //获取实际支付日期与这个日期的间隔天数相比
        candidateList.add(new Pair<>(Math.abs(LocalDateTimeUtil.between(actualPayDate.atStartOfDay(), standardDateLastMonth.atStartOfDay(), ChronoUnit.DAYS)), standardDateLastMonth));
        candidateList.add(new Pair<>(Math.abs(LocalDateTimeUtil.between(actualPayDate.atStartOfDay(), standardDateThisMonth.atStartOfDay(), ChronoUnit.DAYS)), standardDateThisMonth));
        candidateList.add(new Pair<>(Math.abs(LocalDateTimeUtil.between(actualPayDate.atStartOfDay(), standardDateNextMonth.atStartOfDay(), ChronoUnit.DAYS)), standardDateNextMonth));
        candidateList.sort(Comparator.comparing(Pair::getKey));
        //取最近的一天
        return candidateList.get(0).getValue();
    }

    /**
     * 年利率转换为期利率
     *
     * @param interestRate  年利率
     * @param repayRateEnum 还款频率
     * @return 期利率
     */
    public static BigDecimal transformRate(Integer interestRate, RepayRateEnum repayRateEnum) {
        // 转换利率
        BigDecimal b = BigDecimal.valueOf(interestRate)
                .divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 4, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        return b.divide(BigDecimal.valueOf(getRepayTimesInYear(repayRateEnum)), 15, RoundingMode.HALF_UP);
    }

    public static BigDecimal toYearIRR(BigDecimal irr, RepayRateEnum repayRateEnum) {
        return irr.multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(getRepayIntervalMonth(repayRateEnum)), RoundingMode.HALF_UP);
    }

    /**
     * 获取年还款次数
     *
     * @param repayRateEnum 还款频率
     * @return 年还款次数
     */
    public static int getRepayTimesInYear(RepayRateEnum repayRateEnum) {
        switch (repayRateEnum) {
            case DOUBLE_MONTH:
                return 6;
            case QUARTER:
                return 4;
            case HALF_YEAR:
                return 2;
            case YEAR:
                return 1;
            default: {
                return 12;
            }
        }
    }

    public static int calcRepayTimes(int totalMonth, String repayRateName) {
        return totalMonth / getRepayIntervalMonth(repayRateName);
    }

    public static Integer getRepayTimesInYear(String repayRateName) {
        RepayRateEnum repayRateEnum = RepayRateEnum.of(repayRateName);
        if (Objects.isNull(repayRateEnum)) {
            return null;
        }
        try {
            return getRepayTimesInYear(repayRateEnum);
        } catch (MithrasException e) {
            return null;
        } catch (Exception e) {
            log.error("获取年还款次数发生未知异常[{}]", repayRateName, e);
            return null;
        }
    }

    /**
     * 获取还款间隔月数
     *
     * @param repayRateEnum 还款频率
     * @return 间隔月数
     */
    public static int getRepayIntervalMonth(RepayRateEnum repayRateEnum) {
        return 12 / getRepayTimesInYear(repayRateEnum);
    }

    public static int getRepayIntervalMonth(String repayRateName) {
        RepayRateEnum repayRateEnum = RepayRateEnum.of(repayRateName);
        if (Objects.isNull(repayRateEnum)) {
            throw new MithrasException("未定义的还款频率类型");
        }
        return 12 / getRepayTimesInYear(repayRateEnum);
    }

    private static List<CashFlowIRRBO.CashFlowAdjustBO> adjustPhase(RepayRateEnum repayRate, List<CashFlowBO> cashFlowList) {
        int repayIntervalMonth = getRepayIntervalMonth(repayRate);
        // 取第0期标准日期
        CashFlowBO zeroFlowBO = findZeroPhase(cashFlowList);
        LocalDate zeroStandardDate = zeroFlowBO.getStandardCashFlowDate();
        // 调整期项
        List<CashFlowIRRBO.CashFlowAdjustBO> cashFlowAdjustList = new ArrayList<>(cashFlowList.size());
        for (int i = 0; i < cashFlowList.size(); i++) {
            CashFlowBO current = cashFlowList.get(i);
            CashFlowIRRBO.CashFlowAdjustBO cashFlowAdjustBO = new CashFlowIRRBO.CashFlowAdjustBO();
            BeanUtil.copyProperties(current, cashFlowAdjustBO);
            LocalDate currentStandardDate = current.getStandardCashFlowDate();
            int months = i * repayIntervalMonth;
            if (Objects.isNull(currentStandardDate)) {
                // 为空的需要先计算出标准日期
                currentStandardDate = zeroStandardDate.plusMonths(months);
                if (i == cashFlowList.size() - 1) {
                    // 最后一期需要减1天
                    currentStandardDate = currentStandardDate.minusDays(1);
                }
            }
            if (currentStandardDate.isEqual(current.getCashFlowDate())) {
                // 无需调整
                cashFlowAdjustBO.setAdjustCashFlowDate(currentStandardDate);
                cashFlowAdjustBO.setAdjustCashFlowPhase(BigDecimal.valueOf(current.getCashFlowPhase()));
            } else {
                // 调整后期项 = （标准日期-第0期标准日期）间隔的月份/还款频率 + （实际日期-标准日期）/（30*还款频率）
                long days = LocalDateTimeUtil.between(currentStandardDate.atStartOfDay(), current.getCashFlowDate().atStartOfDay(), ChronoUnit.DAYS);
                BigDecimal adjustPhase = BigDecimal.valueOf(months).divide(BigDecimal.valueOf(repayIntervalMonth), 15, RoundingMode.HALF_UP)
                        .add(BigDecimal.valueOf(days).divide(BigDecimal.valueOf(30 * repayIntervalMonth), 15, RoundingMode.HALF_UP));
                cashFlowAdjustBO.setAdjustCashFlowDate(currentStandardDate);
                cashFlowAdjustBO.setAdjustCashFlowPhase(adjustPhase);
            }
            cashFlowAdjustList.add(cashFlowAdjustBO);
        }
        return cashFlowAdjustList;
    }

    @Deprecated
    private static List<CashFlowIRRBO.CashFlowAdjustBO> adjustPhaseByNoRule(List<CashFlowBO> cashFlowList) {
        // 调整期项
        List<CashFlowIRRBO.CashFlowAdjustBO> cashFlowAdjustList = new ArrayList<>(cashFlowList.size());
        // 取第0期
        CashFlowBO zeroFlowBO = findZeroPhase(cashFlowList);
        LocalDate zeroStandardDate = zeroFlowBO.getStandardCashFlowDate();
        // 处理剩余数据
        for (int i = 1; i < cashFlowList.size(); i++) {
            CashFlowBO cashFlowBO = cashFlowList.get(i);
            CashFlowIRRBO.CashFlowAdjustBO cashFlowAdjustBO = new CashFlowIRRBO.CashFlowAdjustBO();
            BeanUtil.copyProperties(cashFlowBO, cashFlowAdjustBO);
            LocalDate adjustCashFlowDate = LocalDate.of(cashFlowBO.getCashFlowDate().getYear(), cashFlowBO.getCashFlowDate().getMonthValue(), zeroStandardDate.getDayOfMonth());
            cashFlowAdjustBO.setAdjustCashFlowDate(adjustCashFlowDate);
            // 调整期项 =（标准日期-第0期标准日期）间隔的月份 + （实际日期-标准日期） / 30
            long months = LocalDateTimeUtil.between(zeroStandardDate.atStartOfDay(), adjustCashFlowDate.atStartOfDay(), ChronoUnit.MONTHS);
            long days = LocalDateTimeUtil.between(adjustCashFlowDate.atStartOfDay(), cashFlowBO.getCashFlowDate().atStartOfDay(), ChronoUnit.DAYS);
            BigDecimal adjustPhase = BigDecimal.valueOf(months).add(BigDecimal.valueOf(days).divide(BigDecimal.valueOf(30), 15, RoundingMode.HALF_UP));
            cashFlowAdjustBO.setAdjustCashFlowPhase(adjustPhase);
            cashFlowAdjustList.add(cashFlowAdjustBO);
        }
        return cashFlowAdjustList;
    }

    @Deprecated
    private static List<CashFlowIRRBO.CashFlowAdjustBO> adjustPhaseByRule(RepayRateEnum repayRate, List<CashFlowBO> cashFlowList) {
        int monthAdd = getRepayIntervalMonth(repayRate);
        // 调整期项
        List<CashFlowIRRBO.CashFlowAdjustBO> cashFlowAdjustList = new ArrayList<>(cashFlowList.size());
        // 取第0期
        CashFlowBO zeroFlowBO = findZeroPhase(cashFlowList);
        CashFlowIRRBO.CashFlowAdjustBO zeroCashFlowAdjustBO = new CashFlowIRRBO.CashFlowAdjustBO();
        BeanUtil.copyProperties(zeroFlowBO, zeroCashFlowAdjustBO);
        zeroCashFlowAdjustBO.setAdjustCashFlowDate(zeroFlowBO.getStandardCashFlowDate());
        if (zeroFlowBO.getCashFlowDate().isEqual(zeroFlowBO.getStandardCashFlowDate())) {
            zeroCashFlowAdjustBO.setAdjustCashFlowPhase(BigDecimal.ZERO);
        } else {
            long molecular = LocalDateTimeUtil.between(zeroFlowBO.getStandardCashFlowDate().atStartOfDay(), zeroFlowBO.getCashFlowDate().atStartOfDay(), ChronoUnit.DAYS);
            BigDecimal adjustPhase = BigDecimal.valueOf(molecular).divide(BigDecimal.valueOf(monthAdd * 30), 15, RoundingMode.HALF_UP).add(BigDecimal.valueOf(0));
            zeroCashFlowAdjustBO.setAdjustCashFlowPhase(adjustPhase);
        }
        cashFlowAdjustList.add(zeroCashFlowAdjustBO);
        Integer lastStandardPhase = 0;
        LocalDate lastStandardDate = zeroFlowBO.getStandardCashFlowDate();
        // 处理剩余数据
        for (int i = 1; i < cashFlowList.size(); i++) {
            CashFlowBO cashFlowBO = cashFlowList.get(i);
            CashFlowIRRBO.CashFlowAdjustBO cashFlowAdjustBO = new CashFlowIRRBO.CashFlowAdjustBO();
            BeanUtil.copyProperties(cashFlowBO, cashFlowAdjustBO);
            // 没有期项的理论上是付款，特殊处理
            if (Objects.isNull(cashFlowBO.getCashFlowPhase())) {
                int denominator = monthAdd * 30;
                long molecular = LocalDateTimeUtil.between(lastStandardDate.atStartOfDay(), cashFlowBO.getCashFlowDate().atTime(23, 59, 59), ChronoUnit.DAYS);
                BigDecimal adjustPhase = BigDecimal.valueOf(molecular).divide(BigDecimal.valueOf(denominator), 15, RoundingMode.HALF_UP).add(BigDecimal.valueOf(lastStandardPhase));
                cashFlowAdjustBO.setAdjustCashFlowPhase(adjustPhase);
            } else {
                // 根据上一次的标准日期和付款频率计算出本期调整后的日期
                LocalDate currentStandardDate = LocalDateTimeUtil.offset(lastStandardDate.atStartOfDay(), monthAdd, ChronoUnit.MONTHS).toLocalDate();
                if (i == cashFlowList.size() - 1) {
                    // 最后一期需要减1天
                    currentStandardDate = currentStandardDate.minusDays(1);
                }
                boolean isSameDay = cashFlowAdjustBO.getCashFlowDate().isEqual(currentStandardDate);
                // FIXME 先兼容差一天的情况（因为历史原因，一部分数据最后一期没有减一天，一部分数据减了一天）
                // FIXME 需要和产品沟通如果处理
//                if (!isSameDay && (i == cashFlowList.size() - 1)) {
//                    isSameDay = cashFlowAdjustBO.getCashFlowDate().isEqual(currentStandardDate.plusDays(1));
//                }
                if (isSameDay) {
                    // 说明日期一样无需调整
                    cashFlowAdjustBO.setAdjustCashFlowPhase(BigDecimal.valueOf(cashFlowAdjustBO.getCashFlowPhase()));
                    cashFlowAdjustBO.setAdjustCashFlowDate(currentStandardDate);
                } else {
                    // 本期调整后的期项 = 上一期的标准期项 + (本期未调整日期 - 上一期标准（调整后）日期) / (还款频率间隔月数 * 30)
                    long molecular = LocalDateTimeUtil.between(lastStandardDate.atStartOfDay(), cashFlowBO.getCashFlowDate().atStartOfDay(), ChronoUnit.DAYS);
                    int denominator = monthAdd * 30;
                    BigDecimal adjustPhase = BigDecimal.valueOf(molecular).divide(BigDecimal.valueOf(denominator), 15, RoundingMode.HALF_UP).add(BigDecimal.valueOf(lastStandardPhase));
                    // 保存下来
                    cashFlowAdjustBO.setAdjustCashFlowPhase(adjustPhase);
                    cashFlowAdjustBO.setAdjustCashFlowDate(currentStandardDate);
                    cashFlowAdjustBO.setAdjustCashFlowPhaseFM(denominator);
                    cashFlowAdjustBO.setAdjustCashFlowPhaseFZ((int) molecular);
                }
                lastStandardDate = currentStandardDate;
                lastStandardPhase = cashFlowBO.getCashFlowPhase();
            }
            cashFlowAdjustList.add(cashFlowAdjustBO);
        }
        return cashFlowAdjustList;
    }

    private static CashFlowBO findZeroPhase(List<CashFlowBO> cashFlowList) {
        // 根据日期排序
        cashFlowList.sort(Comparator.comparing(CashFlowBO::getCashFlowDate));
        CashFlowBO zeroFlowBO = cashFlowList.get(0);
        if (Objects.isNull(zeroFlowBO.getCashFlowPhase()) || zeroFlowBO.getCashFlowPhase() != 0) {
            // 这个地方再查一遍，兼容存在第0期数据但是还款日期在放款日期之前的情况
            List<CashFlowBO> bos = ListUtil.toCopyOnWriteArrayList(cashFlowList).stream()
                    .filter(item -> Objects.equals(item.getCashFlowPhase(), 0))
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(bos)) {
                throw new MithrasException("第0期数据还款日期应该在放款日期之后，请检查");
            }
            throw new MithrasException("没有第0期数据，无法计算");
        }
        if (Objects.isNull(zeroFlowBO.getStandardCashFlowDate())) {
            // 兼容老逻辑，如果第0期标准日期为空就用实际日期填充
            zeroFlowBO.setStandardCashFlowDate(zeroFlowBO.getCashFlowDate());
        }
        return zeroFlowBO;
    }
}
