package cn.zswltech.mithras.others.service.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.core.application.dto.FinancialCostsBO;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.application.orchestration.contract.ContractRentActualService;
import cn.zswltech.mithras.application.orchestration.contract.impl.ContractReceiptServiceImpl;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.archive.service.ContractReceiptLibService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/8/27/10:24
 * @description
 */
@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "uat")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContractReceiptTest {

    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractReceiptLibService contractReceiptLibService;
    @Autowired
    private ContractReceiptServiceImpl contractReceiptServiceImpl;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;

    @Test
    public void getFinancialCostsTest() {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name())));
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(contractReceiptList)) {
                continue;
            }
            for (ContractReceipt contractReceipt : contractReceiptList) {
                BigDecimal taxRate = GlobalConstants.TAX_RATE_FEI_ZHI_ZU;
                if (StrUtil.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                    taxRate = GlobalConstants.TAX_RATE_ZHI_ZU;
                }
                List<ContractRentActual> contractRentActualList = SpringUtil.getBean(ContractRentActualService.class).listByReceipt(contractReceipt.getId());
                try {
                    FinancialCostsBO financialCostsBO = contractReceiptServiceImpl.getFinancialCosts(contractBaseInfo, contractRentActualList, taxRate, contractReceipt.getId());
                    contractReceipt.setStampDutyTaxRateZL(financialCostsBO.getStampDutyContextBO().getTaxRateZL());
                    contractReceipt.setStampDutyTaxRateMM(financialCostsBO.getStampDutyContextBO().getTaxRateMM());
                    contractReceipt.setActualPayAmount(financialCostsBO.getStampDutyContextBO().getActualPayAmount().longValue());
                    contractReceipt.setActualServiceFeeWithoutTax(financialCostsBO.getStampDutyContextBO().getServiceFeeWithoutTax().longValue());
                    contractReceipt.setStampDutyZL(financialCostsBO.getStampDutyContextBO().getTaxZL().longValue());
                    contractReceipt.setStampDutyMM(financialCostsBO.getStampDutyContextBO().getTaxMM().longValue());
                    contractReceiptService.updateById(contractReceipt);
                } catch (Exception e) {
                    log.error("借据{}更新印花税过程数据发生异常", contractReceipt.getReceiptCode(), e);
                }
            }
        }
    }

    @Test
    public void initReceiptStartDate() {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list();
        StringBuilder errorContractCode = new StringBuilder();
        Map<Long, List<ContractReceipt>> contractIdReceiptListMap = contractReceiptService.list().stream().collect(Collectors.groupingBy(ContractReceipt::getContractId));
        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
            List<ContractReceipt> receiptList = contractIdReceiptListMap.get(contractBaseInfo.getId());
            if (receiptList != null && !receiptList.isEmpty() && receiptList.size() > 1) {
                log.error("合同编号:{}存在多借据，实际起租日期初始化失败", contractBaseInfo.getContractCode());
                errorContractCode.append(contractBaseInfo.getContractCode()).append(",");
                continue;
            }
            if (receiptList != null && !receiptList.isEmpty()) {
                ContractReceipt contractReceipt = receiptList.get(0);
                contractReceipt.setReceiptStartDate(contractBaseInfo.getActualLeaseDate());
                contractReceiptService.lambdaUpdate()
                        .set(ContractReceipt::getReceiptStartDate, contractBaseInfo.getActualLeaseDate())
                        .eq(ContractReceipt::getId, contractReceipt.getId())
                        .update();
                contractReceiptLibService.lambdaUpdate()
                        .eq(ContractReceiptLib::getOriginId, contractReceipt.getId())
                        .set(ContractReceiptLib::getReceiptStartDate, contractBaseInfo.getActualLeaseDate())
                        .update();
                log.info("合同编号:{}实际起租日期初始化成功", contractBaseInfo.getContractCode());
            } else {
                log.error("合同编号:{}不存在借据，实际起租日期初始化失败", contractBaseInfo.getContractCode());
                errorContractCode.append(contractBaseInfo.getContractCode()).append(",");
            }
        }
        log.info("实际起租日期初始化失败的ERR-MSG:{}", errorContractCode);
    }

    @Test
    public void calculationXirr() {
        //查询借据
        List<ContractReceipt> contractReceipts = contractReceiptService.list(Wrappers.<ContractReceipt>lambdaQuery()
                .in(ContractReceipt::getContractId, 1774L)
                );
        //查询借据下租金表
        Map<Long, List<ContractRentActual>> contractReceiptId2Rent = contractRentActualService.list().stream().filter(e -> ObjectUtil.isNotEmpty(e.getReceiptId())).collect(Collectors.groupingBy(ContractRentActual::getReceiptId));
        //查询合同下首期利息
        Map<Long, Long> paymentId2Amount = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .eq(CollectionBaseInfo::getPhase, YesOrNoNumberEnum.NO.getCode())).stream().collect(Collectors.toMap(CollectionBaseInfo::getPaymentId, CollectionBaseInfo::getPlanCollectionAmount, (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));

        //付款下咨询费
        Map<Long, Long> paymentId2Other = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.OTHERAMOUNT.name(), CashFlowItemEnum.COMMISSION.name())
        ).stream().collect(Collectors.toMap(CollectionBaseInfo::getPaymentId, CollectionBaseInfo::getPlanCollectionAmount, (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));

        //付款下保证金
        Map<Long, Long> paymentId2EarnestMoney = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.EARNEST_MONEY.name())
        ).stream().collect(Collectors.toMap(CollectionBaseInfo::getPaymentId, CollectionBaseInfo::getPlanCollectionAmount, (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        //付款下咨询费
        Map<Long, Long> paymentId2FirstRent = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.FIRST_RENT.name())
        ).stream().collect(Collectors.toMap(CollectionBaseInfo::getPaymentId, CollectionBaseInfo::getPlanCollectionAmount, (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));


        //查询借据下付款详情
        Map<Long, List<PaymentBaseInfo>> contractReceiptId2Payment = paymentBaseInfoService.list().stream().filter(e -> ObjectUtil.isNotEmpty(e.getReceiptId())).collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptId));

        Map<Long, Map<LocalDate, Long>> paymentId2Actual = new HashMap<>();
        paymentActualDetailService.list().forEach(e -> {
            Map<LocalDate, Long> localDateLongMap = paymentId2Actual.getOrDefault(e.getPaymentId(), new HashMap<>());
            localDateLongMap.put(e.getPaidInDate(), localDateLongMap.getOrDefault(e.getPaidInDate(), 0L) + LongUtil.null2zero(e.getPaidInAmount()));
            paymentId2Actual.put(e.getPaymentId(), localDateLongMap);
        });

        for(ContractReceipt receipt : contractReceipts) {
            List<PaymentBaseInfo> paymentBaseInfos = contractReceiptId2Payment.get(receipt.getId());
            List<ContractRentActual> contractRentActuals = contractReceiptId2Rent.get(receipt.getId());
            if(ObjectUtil.isEmpty(paymentBaseInfos) || ObjectUtil.isEmpty(contractRentActuals)) {
                continue;
            }
            //收集付款详情
            Map<LocalDate, Long> paymentActualDetails = paymentBaseInfos.stream().map(e -> paymentId2Actual.get(e.getId())).filter(ObjectUtil::isNotEmpty).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.summingLong(Map.Entry::getValue)
                    ));

            if (ObjectUtil.isEmpty(paymentActualDetails)) {
                continue;
            }
            for(LocalDate k : paymentActualDetails.keySet()) {
                ContractRentActual t = new ContractRentActual();
                t.setRent(-paymentActualDetails.get(k));
                t.setCashFlowDate(k);
                contractRentActuals.add( t);
            }
            contractRentActuals = contractRentActuals.stream().sorted(Comparator.comparing(ContractRentActual::getCashFlowDate))
                    .collect(Collectors.toList());
            //第一笔付款
            Long sum = 0L;
            Long lastSum = 0L;
            for (PaymentBaseInfo e : paymentBaseInfos) {
                sum = paymentId2Amount.getOrDefault(e.getId(), 0L) +
                        paymentId2FirstRent.getOrDefault(e.getId(), 0L) +
                        paymentId2Other.getOrDefault(e.getId(), 0L) +
                        paymentId2EarnestMoney.getOrDefault(e.getId(), 0L)
                       ;
                lastSum = LongUtil.null2zero(e.getNominalPrice()) - paymentId2EarnestMoney.getOrDefault(e.getId(), 0L);
            }
            contractRentActuals.get(0).setRent(LongUtil.null2zero(contractRentActuals.get(0).getRent()) + sum);

            ContractRentActual lastRentActual = contractRentActuals.get(contractRentActuals.size() - 1);
            lastRentActual.setRent(LongUtil.null2zero(lastRentActual.getRent()) + LongUtil.null2zero(lastSum));
            double v = calculateXIRR(contractRentActuals);
            receipt.setXirr(v);
        }
        contractReceiptService.updateBatchById(contractReceipts);

    }


    public double calculateXIRR(List<ContractRentActual> actualRentList) {
        try {
            if (CollectionUtil.isEmpty(actualRentList)) {
                return 0.0;
            }
            // 参数校验
            List<Double> amounts = new ArrayList<>();
            List<LocalDate> dates = new ArrayList<>();
            for (ContractRentActual cashFlowExcelModel : actualRentList) {
                if (ObjectUtil.isNotEmpty(cashFlowExcelModel) && ObjectUtil.isNotEmpty(cashFlowExcelModel.getRent()) && ObjectUtil.isNotEmpty(cashFlowExcelModel.getCashFlowDate())) {
                    amounts.add(LongUtil.tenThousand2Dollar(cashFlowExcelModel.getRent()).doubleValue());
                    dates.add(cashFlowExcelModel.getCashFlowDate());
                }
            }
            // 检查正负现金流
            boolean hasPositive = false;
            boolean hasNegative = false;
            for (Double amount : amounts) {
                if (amount > 0) hasPositive = true;
                if (amount < 0) hasNegative = true;
            }
            if (!(hasPositive && hasNegative)) {
                log.warn("calculateXIRR error 现金流必须包含正负值");
                return 0.0;
            }
            // 获取最早日期并计算时间差
            LocalDate firstDate = Collections.min(dates);
            double[] years = new double[dates.size()];
            for (int i = 0; i < dates.size(); i++) {
                long daysBetween = ChronoUnit.DAYS.between(firstDate, dates.get(i));
                years[i] = daysBetween / 365.0;
            }

            // 牛顿迭代参数
            double guess = 0.1;  // 初始猜测10%
            int maxIteration = 1000;
            double precision = 1e-6;

            // 开始迭代计算
            for (int i = 0; i < maxIteration; i++) {
                double npv = 0.0;
                double derivative = 0.0;

                for (int j = 0; j < amounts.size(); j++) {
                    double amount = amounts.get(j);
                    double time = years[j];

                    if (guess <= -1.0) {
                        guess = -0.99999;  // 防止无效计算
                    }

                    double denominator = Math.pow(1 + guess, time);
                    npv += amount / denominator;
                    derivative += -amount * time / (denominator * (1 + guess));
                }

                // 检查收敛
                if (Math.abs(npv) < precision) {
                    return guess;
                }

                // 防止除零错误
                if (Math.abs(derivative) < precision) {
                    log.warn("calculateXIRR error 无法收敛（导数过小）");
                    return 0.0;
                }

                // 更新猜测值
                double newGuess = guess - npv / derivative;

                // 限制有效范围
                if (newGuess <= -1.0) {
                    newGuess = -0.9999;
                }

                guess = newGuess;
            }

            log.warn("calculateXIRR error 经过 " + maxIteration + " 次迭代未收敛");
        } catch (Exception e) {
            log.warn("xirr计算错误，不影响正常业务", e);
        }
        return 0.0;
    }
}
