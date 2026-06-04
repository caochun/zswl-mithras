package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 新增加权融资成本  本年新增融资加权融资成本 实际付款核销日是当年的合同，按剩余本金加权的FTP价（剩余本金=付款核销金额-已核销首期租金-已核销本金）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Slf4j
@Component
public class FCM_093Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private FtpAssessmentInfoReader ftpAssessmentInfoReader;

    @Override
    public String metricCode() {
        return "FCM_093";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        LocalDate start = dateTime.with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = dateTime.with(TemporalAdjusters.lastDayOfYear());
        // 本年新增融资
        List<PaymentActualDetail> detailList = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                .ge(PaymentActualDetail::getPaidInDate, start)
                .le(PaymentActualDetail::getPaidInDate, end));

        if (ObjectUtil.isEmpty(detailList)) {
            return BigDecimal.ZERO;
        }

        // 何元说，钱只取当年，借据拿不到的就不算了
        List<Long> paymentIds = detailList.stream().map(PaymentActualDetail::getPaymentId).collect(Collectors.toList());
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectBatchIds(paymentIds);
        // 寻找借据
        List<ContractReceipt> contractReceiptList = contractReceiptMapper.selectList(Wrappers.<ContractReceipt>lambdaQuery()
                // 这里暂时只查生效的
                .in(ContractReceipt::getId, paymentBaseInfos.stream().map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toList())));
        if (ObjectUtil.isEmpty(contractReceiptList)) {
            return BigDecimal.ZERO;
        }

        // 做映射
        Map<Long, ContractReceipt> receiptMap = contractReceiptList.stream().collect(Collectors.toMap(ContractReceipt::getId, Function.identity(), (v1, v2) -> v1));
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = paymentBaseInfos.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, Function.identity(), (v1, v2) -> v1));

        // 需要做一下过滤
        List<PaymentActualDetail> actualDetails = new ArrayList<>();
        for (PaymentActualDetail actualDetail : detailList) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMap.get(actualDetail.getPaymentId());
            if (Objects.isNull(paymentBaseInfo)) {
                continue;
            }
            ContractReceipt receipt = receiptMap.get(paymentBaseInfo.getReceiptIdFinal());
            if (Objects.isNull(receipt)) {
                continue;
            }
            actualDetails.add(actualDetail);
        }
        if (actualDetails.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 获取加权平均值
        BigDecimal average = BigDecimal.ZERO;
        // 加总
        BigDecimal total = actualDetails.stream().mapToLong(PaymentActualDetail::getPaidInAmount).mapToObj(BigDecimal::new).reduce(BigDecimal::add).get();
        for (PaymentActualDetail actualDetail : actualDetails) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMap.get(actualDetail.getPaymentId());
            ContractReceipt receipt = receiptMap.get(paymentBaseInfo.getReceiptIdFinal());
            // 这里的ftp是挂在日期上的，可能会有问题
            FtpAssessmentInfo latestEffect = ftpAssessmentInfoReader.findLatestEffect(dateTime, receipt.getId());
            if (Objects.isNull(latestEffect)) {
                log.error("找不到最新的ftp信息，paymentCode:{}", paymentBaseInfo.getPaymentCode());
                continue;
            }
            average = new BigDecimal(actualDetail.getPaidInAmount())
                    .multiply(new BigDecimal(LongUtil.null2zero(latestEffect.getAssessmentPrice())))
                    // 因为比例也是存的毫厘，所以需要除以10000
                    .divide(new BigDecimal(10000), 20, RoundingMode.HALF_UP)
                    .divide(total, 4, RoundingMode.HALF_UP)
                    .add(average);
        }
        return average;
    }
}
