package cn.zswltech.mithras.dashboard.application.boss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListRSP;
import cn.zswltech.mithras.dashboard.enums.DashBoardQueryTypeEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.pricing.dto.ContractPriceQueryDto;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/10/23 18:54
 * @description
 */
@Slf4j
@Service
public class CurrentYearBusinessPayReceiptService implements CurrentYearBusinessPayReceiptApplicationService {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private BusinessMonthCollectService businessMonthCollectService;

    public CurrentYearBusinessPayReceiptRateListRSP payReceiptRateList(CurrentYearBusinessPayReceiptRateListREQ req) {
        LocalDate now = LocalDate.now();
        // 1、找到当年的所有投放
        List<PaymentActualDetail> paymentActualDetails = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .between(PaymentActualDetail::getPaidInDate, now.with(TemporalAdjusters.firstDayOfYear()), now.with(TemporalAdjusters.lastDayOfYear())));
        if (CollUtil.isEmpty(paymentActualDetails)) {
            return null;
        }
        // 2、找到所有的合同
        List<Long> contractIds = paymentActualDetails.stream().map(PaymentActualDetail::getContractId).distinct().collect(Collectors.toList());
        Map<Long, ContractBaseInfo> contractMap = contractBaseInfoService.listByIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity(), (a, b) -> a));
        // 3、找到所有合同的报价方案
        List<ContractPriceQueryDto> queryPriceDtoList = contractBaseInfoMapper.queryPriceDtoList(contractIds);
        Map<Long, Long> irrMap = new HashMap<>();
        if (CollUtil.isNotEmpty(queryPriceDtoList)) {
            irrMap = queryPriceDtoList.stream().filter(e -> Objects.nonNull(e.getIrr()))
                    .collect(Collectors.toMap(ContractPriceQueryDto::getContractId, ContractPriceQueryDto::getIrr, (a, b) -> a));
        }

        Map<Long, Long> finalIrrMap = irrMap;
        paymentActualDetails = paymentActualDetails.stream().filter(item -> queryPriceDtoList.stream().map(ContractPriceQueryDto::getContractId).collect(Collectors.toList()).contains(item.getContractId())  &&
                Objects.nonNull(finalIrrMap.get(item.getContractId()))).collect(Collectors.toList());
        // 将浙江业务部和公用事业业务部找到
        Long zjDept = sysUserService.getOrgIdByCode("JCSSYWB");
        Long ggDept = sysUserService.getOrgIdByCode("GGSY");
        List<Long> zjAndGgDeptList = CollUtil.newArrayList(zjDept, ggDept);
        CurrentYearBusinessPayReceiptRateListRSP rsp = new CurrentYearBusinessPayReceiptRateListRSP();
        // 4、根据月份分组，计算出月度投放金额，月度投放完成率
        List<CurrentYearBusinessPayReceiptRateListRSP.Month> monthList = new LinkedList<>();
        for (int i = 1; i < 13; i++) {
            LocalDate beginDate = now.with(TemporalAdjusters.firstDayOfYear()).plusMonths(i - 1);
            LocalDate endDate = beginDate.with(TemporalAdjusters.lastDayOfMonth());

            log.info("当前月度：{}-{}", beginDate, endDate);
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetails.stream()
                    .filter(item -> !item.getPaidInDate().isBefore(beginDate) && !item.getPaidInDate().isAfter(endDate))
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(paymentActualDetailList)) {
                continue;
            }
            // 按照风控行业分组
            List<ContractBaseInfo> contractBaseInfos = new LinkedList<>();
            paymentActualDetailList.stream().map(PaymentActualDetail::getContractId).distinct().collect(Collectors.toList())
                    .forEach(item -> contractBaseInfos.add(contractMap.get(item)));
            List<ContractBaseInfo> publicList = new LinkedList<>();
            List<ContractBaseInfo> industryList = new LinkedList<>();
            contractBaseInfos.forEach(item -> {
                String type = businessMonthCollectService.getType(item, zjAndGgDeptList);
                if (CharSequenceUtil.equals(type, DashBoardQueryTypeEnum.INDUSTRY_CATEGORY.name())) {
                    industryList.add(item);
                } else {
                    publicList.add(item);
                }
            });

            // 每个月的类型分组之后进行统计
            CurrentYearBusinessPayReceiptRateListRSP.Month month = new CurrentYearBusinessPayReceiptRateListRSP.Month();
            month.setMonth(beginDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            ValueUnitDTO industryRate = new ValueUnitDTO();
            List<PaymentActualDetail> industryDetailList = paymentActualDetailList.stream().filter(item -> industryList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()).contains(item.getContractId())).collect(Collectors.toList());
            long industryTotal = industryDetailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).summaryStatistics().getSum();
            BigDecimal industryRateSum = BigDecimal.ZERO;
            for (PaymentActualDetail item : industryDetailList) {
                if (industryTotal == 0) {
                    continue;
                }
                industryRateSum = industryRateSum.add(BigDecimal.valueOf(LongUtil.null2zero(irrMap.get(item.getContractId())))
                        .multiply(BigDecimal.valueOf(LongUtil.null2zero(item.getPaidInAmount())))
                        .divide(BigDecimal.valueOf(industryTotal), 20, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(10000), 20, RoundingMode.HALF_UP));
            }
            industryRate.setValue(industryRateSum.setScale(2, RoundingMode.HALF_UP).toPlainString());
            industryRate.setUnit("%");
            month.setIndustryPayReceiptRate(industryRate);
            ValueUnitDTO publicRate = new ValueUnitDTO();
            List<PaymentActualDetail> publicDetailList = paymentActualDetailList.stream().filter(item -> publicList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()).contains(item.getContractId())).collect(Collectors.toList());
            long publicTotal = publicDetailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).summaryStatistics().getSum();
            BigDecimal publicRateSum = BigDecimal.ZERO;
            for (PaymentActualDetail item : publicDetailList) {
                if (publicTotal == 0) {
                    continue;
                }
                publicRateSum = publicRateSum.add(BigDecimal.valueOf(LongUtil.null2zero(irrMap.get(item.getContractId())))
                        .multiply(BigDecimal.valueOf(LongUtil.null2zero(item.getPaidInAmount())))
                        .divide(BigDecimal.valueOf(publicTotal), 20, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(10000), 20, RoundingMode.HALF_UP));
            }
            publicRate.setValue(publicRateSum.setScale(2, RoundingMode.HALF_UP).toPlainString());
            publicRate.setUnit("%");
            month.setPublicPayReceiptRate(publicRate);
            monthList.add(month);
        }
        // 全年平均
        long sum = paymentActualDetails.stream().mapToLong(PaymentActualDetail::getPaidInAmount).summaryStatistics().getSum();
        ValueUnitDTO average = new ValueUnitDTO();
        BigDecimal yearRate = BigDecimal.ZERO;
        for (PaymentActualDetail item : paymentActualDetails) {
            yearRate = yearRate.add(BigDecimal.valueOf(LongUtil.null2zero(irrMap.get(item.getContractId())))
                    .multiply(BigDecimal.valueOf(LongUtil.null2zero(item.getPaidInAmount())))
                    .divide(BigDecimal.valueOf(sum), 20, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(10000), 20, RoundingMode.HALF_UP));
        }
        average.setValue(yearRate.setScale(2, RoundingMode.HALF_UP).toPlainString());
        average.setUnit("%");
        rsp.setAverage(average);
        monthList.sort(Comparator.comparing(CurrentYearBusinessPayReceiptRateListRSP.Month::getMonth));
        rsp.setMonthList(monthList);
        return rsp;
    }
}
