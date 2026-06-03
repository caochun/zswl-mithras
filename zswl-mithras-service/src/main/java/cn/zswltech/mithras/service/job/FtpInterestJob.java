package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionRecordWriteOffStatus;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.service.service.ftp.FtpInterestDetailRecordService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/5/23
 * @description
 */
@Slf4j
@Component
public class FtpInterestJob {
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private FtpInterestDetailRecordService ftpInterestDetailRecordService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;

    @XxlJob("calculateFtpInterest")
    public void calculateFtpInterest() {
        try {
            LocalDate interestStartDate;
            LocalDate interestEndDate;
            Long targetFtpInterestId = null;
            String param = XxlJobHelper.getJobParam();
//            String param = "{\"ftpInterestId\":326,\"interestStartDate\":\"2023-10-01\",\"interestEndDate\":\"2023-10-31\"}";
            if (StrUtil.isNotBlank(param)) {
                log.info("FTP计息任务 - 控制台参数: {}", param);
                JSONObject jsonObject = JSONUtil.parseObj(param);
                targetFtpInterestId = jsonObject.getLong("ftpInterestId");
                String interestStartDateStr = jsonObject.getStr("interestStartDate");
                String interestEndDateStr = jsonObject.getStr("interestEndDate");
                if (StrUtil.isBlank(interestStartDateStr) || StrUtil.isBlank(interestEndDateStr)) {
                    log.error("FTP计息任务-手动调用控制台参数不符合要求[{}]", param);
                    throw new MithrasException("FTP计息任务-手动调用控制台参数不符合要求");
                }
                interestStartDate = LocalDateTimeUtil.parseDate(interestStartDateStr, DatePattern.NORM_DATE_PATTERN);
                interestEndDate = LocalDateTimeUtil.parseDate(interestEndDateStr, DatePattern.NORM_DATE_PATTERN);
            } else {
                LocalDate now = LocalDate.now();
                interestStartDate = now;
                interestEndDate = now;
            }
            this.calculateFtpInterest(targetFtpInterestId, interestStartDate, interestEndDate);
        } catch (Exception e) {
            log.error("FTP计息任务执行异常", e);
        }
    }

    public void calculateFtpInterest(Long targetFtpInterestId, LocalDate startDate, LocalDate endDate) {
        log.info("FTP计息任务开始执行，开始日期:{},结束日期:{}", startDate, endDate);
        // 分页处理
        int start = 0;
        int limit = 50;
        int count = 0;
        LambdaQueryWrapper<FtpInterestBaseInfo> query = Wrappers.lambdaQuery();
        if (Objects.nonNull(targetFtpInterestId)) {
            // 如果指定了FtpInterestId则忽略是否完成的条件，考虑计息完成情况下的数据重算场景
            query.eq(FtpInterestBaseInfo::getId, targetFtpInterestId);
        } else {
            query.eq(FtpInterestBaseInfo::getFinish, YesOrNoNumberEnum.NO.getCode());
        }
        // 结合实际业务，不可能有100000个未完成的FTP计息任务
        while (count < 100000) {
            query.last(StringUtil.mysqlLimit(start, limit));
            List<FtpInterestBaseInfo> todoList = ftpInterestBaseInfoService.list(query);
            if (CollectionUtil.isEmpty(todoList)) {
                break;
            }
            for (FtpInterestBaseInfo ftpInterestBaseInfo : todoList) {
                try {
                    LocalDate interestStartDate = startDate;
                    // 判断是否已经有详情
                    int detailCount = ftpInterestDetailRecordService.countByFtpInterestId(ftpInterestBaseInfo.getId());
                    if (detailCount == 0) {
                        // 说明还没有进行过FTP计息，寻找首次核销日期
                        LocalDate contractStartDate = this.findStartDate(ftpInterestBaseInfo);
                        if (Objects.isNull(contractStartDate)) {
                            log.info("{}还未产生实际核销，不进行FTP计息", ftpInterestBaseInfo.getReceiptCode());
                            continue;
                        } else {
                            if (contractStartDate.isBefore(endDate)) {
                                interestStartDate = contractStartDate;
                            } else {
                                continue;
                            }
                        }
                    }
                    ftpInterestBaseInfoService.calculateFtpInterestTimeRange(ftpInterestBaseInfo, interestStartDate, endDate);
                } catch (Exception e) {
                    log.error("执行FTP计息发生异常[ftpInterestId:{}]", ftpInterestBaseInfo.getId(), e);
                }
                try {
                    // 为了考虑跨月核销的场景，在正常计息跑完之后需要做补偿逻辑
                    this.tryInterestHistory(ftpInterestBaseInfo);
                } catch (Exception e) {
                    log.error("执行FTP计息补偿逻辑发生异常[ftpInterestId:{}]", ftpInterestBaseInfo.getId(), e);
                }
            }
            start = start + limit;
            count++;
        }
    }

    private void tryInterestHistory(FtpInterestBaseInfo ftpInterestBaseInfo) {
        LocalDate now = LocalDate.now();
        List<LocalDate> candidateRecalculateStartDateList = new LinkedList<>();
        // 找付款
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(ftpInterestBaseInfo.getReceiptId());
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
            query.in(PaymentActualDetail::getPaymentId, paymentIds);
            query.ge(BaseModel::getCreateTime, now.atStartOfDay());
            query.le(BaseModel::getCreateTime, LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 23, 59, 59));
            query.lt(PaymentActualDetail::getPaidInDate, now);
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.list(query);
            if (CollectionUtil.isNotEmpty(paymentActualDetailList)) {
                paymentActualDetailList.forEach(e -> {
                    if (Objects.nonNull(e.getPaidInDate())) {
                        candidateRecalculateStartDateList.add(e.getPaidInDate());
                    }
                });
            }
        }
        // 找收款
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listByContractIds(Collections.singletonList(ftpInterestBaseInfo.getContractId()));
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            Set<Long> collectionIds = collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<CollectionRecordInfo> query = Wrappers.lambdaQuery();
            query.in(CollectionRecordInfo::getCollectionId, collectionIds);
            query.ge(BaseModel::getCreateTime, now.atStartOfDay());
            query.le(BaseModel::getCreateTime, LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 23, 59, 59));
            query.lt(CollectionRecordInfo::getCollectionDate, now);
            List<CollectionRecordInfo> collectionRecordInfoList = SpringUtil.getBean(CollectionRecordInfoService.class).list(query);
            if (CollectionUtil.isNotEmpty(collectionRecordInfoList)) {
                collectionRecordInfoList.forEach(e -> {
                    if (Objects.nonNull(e.getCollectionDate())) {
                        candidateRecalculateStartDateList.add(e.getCollectionDate());
                    }
                });
            }
        }
        // 排序取最早的日期进行重算
        if (CollectionUtil.isNotEmpty(candidateRecalculateStartDateList)) {
            candidateRecalculateStartDateList.sort(Comparator.comparing(e -> e));
            LocalDate recalculateStartDate = candidateRecalculateStartDateList.get(0);
            ftpInterestBaseInfoService.calculateFtpInterestTimeRange(ftpInterestBaseInfo, recalculateStartDate, now);
        }
    }

    private LocalDate findStartDate(FtpInterestBaseInfo ftpInterestBaseInfo) {
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap;
        ContractReceipt contractReceipt = contractReceiptService.getById(ftpInterestBaseInfo.getReceiptId());
        // 找到借据对应的付款
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(contractReceipt.getId());
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            log.info("没有找到任何付款申请[receiptCode:{}]", contractReceipt.getReceiptCode());
            return null;
        }
//        paymentBaseInfoList.removeIf(p -> Objects.isNull(p.getCashFtpFinal()) || Objects.isNull(p.getBillFtpFinal()));
//        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
//            log.info("没有找到含有生效FTP数据的付款申请[receiptCode:{}]", contractReceipt.getReceiptCode());
//            return null;
//        }
        paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, e -> e));
        Set<Long> paymentIds = paymentBaseInfoMap.keySet();
        // 找到最早的实付日期
        LocalDate earliestPayDate = null;
        LambdaQueryWrapper<PaymentActualDetail> paymentActualDetailQuery = Wrappers.lambdaQuery();
        paymentActualDetailQuery.in(PaymentActualDetail::getPaymentId, paymentIds);
        paymentActualDetailQuery.eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name());
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.list(paymentActualDetailQuery);
        if (CollectionUtil.isNotEmpty(paymentActualDetailList)) {
            paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
            earliestPayDate = paymentActualDetailList.get(0).getPaidInDate();
        }
        // 找到最早的实收日期
        LocalDate earliestCollectDate = null;
        LambdaQueryWrapper<CollectionBaseInfo> collectionBaseInfoQuery = Wrappers.lambdaQuery();
        collectionBaseInfoQuery.eq(CollectionBaseInfo::getContractId, contractReceipt.getContractId());
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(collectionBaseInfoQuery);
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            Iterator<CollectionBaseInfo> iterator = collectionBaseInfoList.iterator();
            while (iterator.hasNext()) {
                CollectionBaseInfo cbi = iterator.next();
                // 只保留归属于借据的或者归属于借据对应的付款的
                if (Objects.equals(cbi.getReceiptId(), contractReceipt.getId())) {
                    continue;
                }
                if (paymentIds.contains(cbi.getPaymentId())) {
                    continue;
                }
                iterator.remove();
            }
            if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
                Map<Long, CollectionBaseInfo> map = collectionBaseInfoList.stream().collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e));
                LambdaQueryWrapper<CollectionRecordInfo> collectionRecordQuery = Wrappers.lambdaQuery();
                collectionRecordQuery.in(CollectionRecordInfo::getCollectionId, map.keySet());
                collectionRecordQuery.eq(CollectionRecordInfo::getWriteOffStatus, CollectionRecordWriteOffStatus.WRITTEN_OFF.name());
                List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.list(collectionRecordQuery);
                collectionRecordInfoList.removeIf(item -> Objects.isNull(item.getCollectionDate()));
                if (CollectionUtil.isNotEmpty(collectionRecordInfoList)) {
                    collectionRecordInfoList.sort(Comparator.comparing(CollectionRecordInfo::getCollectionDate));
                    earliestCollectDate = collectionRecordInfoList.get(0).getCollectionDate();
                }
            }
        }
        if (Objects.nonNull(earliestPayDate) && Objects.nonNull(earliestCollectDate)) {
            if (earliestPayDate.isBefore(earliestCollectDate)) {
                return earliestPayDate;
            } else {
                return earliestCollectDate;
            }
        } else if (Objects.isNull(earliestPayDate) && Objects.isNull(earliestCollectDate)) {
            return null;
        } else if (Objects.nonNull(earliestPayDate)) {
            return earliestPayDate;
        } else {
            return earliestCollectDate;
        }
    }
}
