package cn.zswltech.mithras.application.orchestration.contract.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentAddREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentDetailRSP;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentModifyREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contractcp.ContractInfoRSP;
import cn.zswltech.mithras.dto.contractcp.ContractcpContractDetailREQ;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractPrepaymentMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractPrepayment;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.ContractPrepaymentService;
import cn.zswltech.mithras.contract.core.ContractRentActualService;
import cn.zswltech.mithras.contract.versioning.application.ContractPrepaymentLibService;
import cn.zswltech.mithras.collection.application.contractcp.ContractCollectionPaymentService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ContractPrepaymentServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/8/25 1:39 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class ContractPrepaymentServiceImpl extends ServiceImpl<ContractPrepaymentMapper, ContractPrepayment> implements ContractPrepaymentService {

    @Resource
    private ContractCollectionPaymentService contractCollectionPaymentService;

    @Resource
    ContractPrepaymentLibService contractPrepaymentLibService;

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Resource
    private ContractRentActualService contractRentActualService;

    @Resource
    private CollectionService collectionService;

    @Override
    public ContractPrepayment getList(ContractIdListREQ req) {
        ContractPrepayment contractPrepayment;
        if (ObjectUtil.isNull(req.getVersion())) {
            contractPrepayment = baseMapper.selectOne(Wrappers.<ContractPrepayment>lambdaQuery()
                    .eq(ContractPrepayment::getContractId, req.getContractId())
                    .last(StringUtil.mysqlLimitOne()));
        }else {
            contractPrepayment = contractPrepaymentLibService.getByVersion(req.getContractId(), req.getVersion());
        }
        if (ObjectUtil.isNull(contractPrepayment)) {
            ContractPrepaymentAddREQ contractPrepaymentAddREQ = new ContractPrepaymentAddREQ();
            contractPrepaymentAddREQ.setContractId(req.getContractId());
            calculation(contractPrepaymentAddREQ);
            contractPrepayment = BeanUtil.copyProperties(contractPrepaymentAddREQ, ContractPrepayment.class);
            //获取剩余名义价款
            contractPrepayment.setNominalPrice(collectionService.getRemainingAmountByContract(req.getContractId(), CashFlowItemEnum.NOMINAL_PRICE.name()));
        }
        return contractPrepayment;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ContractPrepaymentDetailRSP add(ContractPrepaymentAddREQ req) {
//        Long loss = req.getLoss();
        calculation(req);
        ContractPrepayment contractPrepayment = BeanUtil.copyProperties(req, ContractPrepayment.class);
        ContractPrepaymentDetailRSP contractPrepaymentDetailRSP;
//        contractPrepayment.setLoss(loss);
        this.check(contractPrepayment);
        baseMapper.insert(contractPrepayment);
        contractPrepaymentDetailRSP = BeanUtil.copyProperties(contractPrepayment, ContractPrepaymentDetailRSP.class);
        return contractPrepaymentDetailRSP;
    }

    @Override
    public void update(ContractPrepaymentModifyREQ req) {
//        Long loss = req.getLoss();
//        ContractPrepaymentAddREQ contractPrepaymentAddREQ = BeanUtil.copyProperties(req, ContractPrepaymentAddREQ.class);
        calculation(req);
//        contractPrepaymentAddREQ.setLoss(loss);
        ContractPrepayment contractPrepayment = BeanUtil.copyProperties(req, ContractPrepayment.class);
//        contractPrepayment.setLoss(loss);
        contractPrepayment.setId(req.getId());
        this.check(contractPrepayment);
        baseMapper.updateAnnotationIncludeNullById(contractPrepayment);
    }

    @Override
    public <T extends ContractPrepaymentAddREQ> void calculation(T req) {
//        ContractcpContractDetailREQ contractcpContractDetailREQ = new ContractcpContractDetailREQ();
//        contractcpContractDetailREQ.setContractId(req.getContractId());
//        ContractInfoRSP contractInfoRSP = contractCollectionPaymentService.contractInfo(contractcpContractDetailREQ);
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listRentByContractId(req.getContractId());
        collectionBaseInfoList.sort(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate));
        // 到期未付租金
        req.setUnpaidRentDue(this.calculateUnpaidRentDue(collectionBaseInfoList, req.getApplayRepaymentDate()));
        // 未到期本金
        req.setBeforeMaturityPrincipal(this.calculateBeforeMaturityPrincipal(collectionBaseInfoList, req.getApplayRepaymentDate()));
        if (Objects.equals(req.getIsEarlySettle(), YesOrNoNumberEnum.YES.getCode())) {
            // 未到期利息
            req.setBeforeMaturityInterest(this.calculatePlanInterest(collectionBaseInfoList, req.getContractId(), req.getBeforeMaturityPrincipal(), req.getApplayRepaymentDate(), true));
            // 提前终止补偿金
            req.setLoss(this.calculateLossSettle(collectionBaseInfoList, req.getBeforeMaturityPrincipal(), req.getApplayRepaymentDate()));
        } else if (Objects.equals(req.getIsEarlySettle(), YesOrNoNumberEnum.NO.getCode())) {
            // 提前归还利息
            req.setEarlyRepaymentInterest(this.calculatePlanInterest(collectionBaseInfoList, req.getContractId(), req.getEarlyRepayment(), req.getApplayRepaymentDate(), false));
            // 提前终止补偿金
            req.setLoss(this.calculateLossNotSettle(collectionBaseInfoList, req.getEarlyRepayment(), req.getApplayRepaymentDate()));
        } else {
            req.setLoss(0L);
        }
        // 保证金余额
        req.setEarnestMoneyBalance(this.calculateEarnestMoneyBalance(req.getContractId()));
        // 违约金（罚息）
        req.setPenalty(this.calculatePenalty(collectionBaseInfoList, req.getContractId(), req.getApplayRepaymentDate()));
//        // 实际租金表
//        if (ObjectUtil.isEmpty(req.getApplayRepaymentDate()) || ObjectUtil.isEmpty(req.getEarlyRepayment())) {
//            return;
//        }
//        List<ContractRentActual> contractRentActualList = contractRentActualService.listByContract(req.getContractId());
//        Assert.notEmpty(contractRentActualList, () -> MithrasException.newException("没有找到实际租金表信息"));
//        // 根据现金流日期排序
//        contractRentActualList.sort(Comparator.comparing(ContractRentActual::getCashFlowDate));
//        long days;
//        Double sumLoss = (double) 0;
//        Long price = 0L;
//        Long priceMount = req.getEarlyRepayment();
//        CollectionBaseInfo collectionBaseInfo;
//        //计算提前终止补偿金
//        for (ContractRentActual contractRentActual : contractRentActualList) {
//            if (contractRentActual.getCashFlowDate().isAfter(req.getApplayRepaymentDate())) {
//                days = req.getApplayRepaymentDate().until(contractRentActual.getCashFlowDate(), ChronoUnit.DAYS);
//                collectionBaseInfo = collectionBaseInfoMap.get(contractRentActual.getId());
//                if (ObjectUtil.isNotEmpty(collectionBaseInfo)) {
//                    //未还本金
//                    price = LongUtil.null2zero(contractRentActual.getPrincipal() - LongUtil.null2zero(collectionBaseInfo.getCollectionPrincipal()));
//                    if (priceMount >= price) {
//                        sumLoss = sumLoss + (double) (price * 2 * days) / (double) (100 * 360);
//                        priceMount -= price;
//                    } else {
//                        sumLoss = sumLoss + (double) (priceMount * 2 * days) / (double) (100 * 360);
//                        break;
//                    }
//                }
//            }
//        }
//        // 保留两位小数到分 Math.round方法只是去掉了毫厘之后的小数点 业务上只需要精确到分即可
//        req.setLoss(Util.mithrasLongDecimalTwo(Math.round(sumLoss)));
    }

    private long calculateUnpaidRentDue(List<CollectionBaseInfo> collectionBaseInfoList, LocalDate targetDate) {
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return 0L;
        }
        if (Objects.isNull(targetDate)) {
            return 0L;
        }
        long amount = 0L;
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            if (!collectionBaseInfo.getPlanCollectionDate().isAfter(targetDate)) {
                long remaining = Optional.ofNullable(collectionBaseInfo.getPlanCollectionAmount()).orElse(0L) - Optional.ofNullable(collectionBaseInfo.getCollectionAmount()).orElse(0L);
                amount = amount + Math.max(remaining, 0L);
            }
        }
        return amount;
    }

    private long calculateBeforeMaturityPrincipal(List<CollectionBaseInfo> collectionBaseInfoList, LocalDate targetDate) {
        if (CollectionUtil.isEmpty(collectionBaseInfoList) || Objects.isNull(targetDate)) {
            return 0L;
        }
        long amount = 0L;
//        LocalDate now = LocalDate.now();
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            if (collectionBaseInfo.getPlanCollectionDate().isAfter(targetDate)) {
                long principal = Optional.ofNullable(collectionBaseInfo.getPrincipal()).orElse(0L) - Optional.ofNullable(collectionBaseInfo.getCollectionPrincipal()).orElse(0L);
                amount = amount + Math.max(principal, 0L);
            }
        }
        return amount;
    }

    private long calculatePlanInterest(List<CollectionBaseInfo> collectionBaseInfoList, Long contractId, Long planPrincipal, LocalDate earlyRepayDate, boolean settle) {
        if (Objects.isNull(planPrincipal)) {
            return 0L;
        }
        if (Objects.isNull(earlyRepayDate)) {
            return 0L;
        }
        // 找租赁利率(仅考虑租赁业务类型)
        ContractLeasePrice contractLeasePrice = SpringUtil.getBean(ContractLeasePriceService.class).getByContractId(contractId);
        if (Objects.isNull(contractLeasePrice)) {
            return 0L;
        }
        Integer lpr = Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0);
        Integer lprAdd = Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0);
        BigDecimal interestRate = BigDecimal.valueOf(lpr + lprAdd).divide(BigDecimal.valueOf(10000 * 100), 20, RoundingMode.HALF_UP);
        // 从后往前找到第一个小于等于提前还款日的应收日并计算天数
        LocalDate targetDate = null;
        for (int i = (collectionBaseInfoList.size() - 1); i >= 0; i--) {
            CollectionBaseInfo collectionBaseInfo = collectionBaseInfoList.get(i);
            // 产品说当天不算在内
            if (!collectionBaseInfo.getPlanCollectionDate().isAfter(earlyRepayDate)) {
                targetDate = collectionBaseInfo.getPlanCollectionDate();
                break;
            }
        }
        if (Objects.isNull(targetDate)) {
            LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
            query.eq(PaymentActualDetail::getContractId, contractId);
            query.eq(PaymentActualDetail::getCancelWriteOffFlag, YesOrNoNumberEnum.NO.getCode());
            List<PaymentActualDetail> paymentActualDetailList = SpringUtil.getBean(PaymentActualDetailService.class).list(query);
            if (CollectionUtil.isEmpty(paymentActualDetailList)) {
                throw new MithrasException("没有找到有效的付款核销记录");
            }
            paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
            if (settle) {
                // 说明还没有还过钱，需要特殊计算
                // 应收利息=∑每笔投放金额*（提前还款日-付款核销实付日期）*利率/360
                BigDecimal result = BigDecimal.ZERO;
                for (PaymentActualDetail paymentActualDetail : paymentActualDetailList) {
                    long days = LocalDateTimeUtil.between(paymentActualDetail.getPaidInDate().atStartOfDay(), earlyRepayDate.atStartOfDay(), ChronoUnit.DAYS);
                    result = result.add(BigDecimal.valueOf(paymentActualDetail.getPaidInAmount()).multiply(BigDecimal.valueOf(days)).multiply(interestRate).divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP));
                }
                return Util.mithrasLongDecimalTwo(result.longValue());
            } else {
                long days = LocalDateTimeUtil.between(paymentActualDetailList.get(0).getPaidInDate().atStartOfDay(), earlyRepayDate.atStartOfDay(), ChronoUnit.DAYS);
                // 应收利息=提前归还本金or未到期本金*(提前还款日-原租金表上个应收日)*租赁利率/360
                BigDecimal result = BigDecimal.valueOf(planPrincipal).multiply(BigDecimal.valueOf(days)).multiply(interestRate).divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP);
                return Util.mithrasLongDecimalTwo(result.longValue());
            }
        } else {
            long days = LocalDateTimeUtil.between(targetDate.atStartOfDay(), earlyRepayDate.atStartOfDay(), ChronoUnit.DAYS);
            // 应收利息=提前归还本金or未到期本金*(提前还款日-原租金表上个应收日)*租赁利率/360
            BigDecimal result = BigDecimal.valueOf(planPrincipal).multiply(BigDecimal.valueOf(days)).multiply(interestRate).divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP);
            return Util.mithrasLongDecimalTwo(result.longValue());
        }
    }

    private long calculateLossNotSettle(List<CollectionBaseInfo> collectionBaseInfoList, Long earlyRepayAmount, LocalDate earlyRepayDate) {
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return 0L;
        }
        if (Objects.isNull(earlyRepayDate)) {
            return 0L;
        }
        if (Objects.isNull(earlyRepayAmount) || earlyRepayAmount <= 0) {
            return 0L;
        }
        // 找到提前归还本金能够覆盖的最晚一起的应收
        BigDecimal available = BigDecimal.valueOf(earlyRepayAmount);
        CollectionBaseInfo targetCollection = null;
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            if (collectionBaseInfo.getPlanCollectionDate().isAfter(earlyRepayDate)) {
                targetCollection = collectionBaseInfo;
                long principal = Optional.ofNullable(collectionBaseInfo.getPrincipal()).orElse(0L) - Optional.ofNullable(collectionBaseInfo.getCollectionPrincipal()).orElse(0L);
                BigDecimal principalB = BigDecimal.valueOf(principal);
                if (principalB.compareTo(available) >= 0) {
                    break;
                } else {
                    // 可用大于目标本金
                    available = available.subtract(principalB);
                }
            }
        }
        // 提前终止补偿金金额 = 提前归还本金 × 2% / 360 ×（提前还款金额能覆盖到的原租金表最晚一期的计划还款日-提前还款日）
        if (Objects.isNull(targetCollection)) {
            return 0L;
        }
        log.info("提前归还本金能够覆盖的原租金表最晚一期的租金信息:{}", JSONUtil.toJsonStr(targetCollection));
        long days = LocalDateTimeUtil.between(earlyRepayDate.atStartOfDay(), targetCollection.getPlanCollectionDate().atStartOfDay(), ChronoUnit.DAYS);
        BigDecimal result = BigDecimal.valueOf(earlyRepayAmount).multiply(BigDecimal.valueOf(days)).multiply(BigDecimal.valueOf(0.02)).divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP);
        return Util.mithrasLongDecimalTwo(result.longValue());
    }

    private long calculateLossSettle(List<CollectionBaseInfo> collectionBaseInfoList, Long planPrincipal, LocalDate earlyRepayDate) {
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return 0L;
        }
        if (Objects.isNull(earlyRepayDate)) {
            return 0L;
        }
        if (Objects.isNull(planPrincipal)) {
            return 0L;
        }
        // 未到期本金 * (2% / 360) * (原租金表最后一期计划还款日 - 提前还款日期)
        CollectionBaseInfo last = collectionBaseInfoList.get(collectionBaseInfoList.size() - 1);
        long days = LocalDateTimeUtil.between(earlyRepayDate.atStartOfDay(), last.getPlanCollectionDate().atStartOfDay(), ChronoUnit.DAYS);
        BigDecimal result = BigDecimal.valueOf(planPrincipal).multiply(BigDecimal.valueOf(0.02).divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(days));
        return Util.mithrasLongDecimalTwo(result.longValue());
    }

    private long calculateEarnestMoneyBalance(Long contractId) {
        return SpringUtil.getBean(MarginBaseInfoService.class).getMarginBalance(contractId);
    }

    private long calculatePenalty(List<CollectionBaseInfo> collectionBaseInfoList, Long contractId, LocalDate earlyRepayDate) {
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return 0L;
        }
        if (Objects.isNull(earlyRepayDate)) {
            return 0L;
        }
        // 仅租赁有罚息日利率，目前只算租赁业务
        ContractLeasePrice contractLeasePrice = SpringUtil.getBean(ContractLeasePriceService.class).getByContractId(contractId);
        if (Objects.isNull(contractLeasePrice) || Objects.isNull(contractLeasePrice.getDefaultInterestRate())) {
            return 0L;
        }
        // 查询实际核销记录
        List<CollectionRecordInfo> collectionRecordInfoList = SpringUtil.getBean(CollectionRecordInfoService.class).listByCollectionIds(collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()));
        Map<Long, List<CollectionRecordInfo>> collectionRecordInfoMap = collectionRecordInfoList.stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
        // 计算总的罚息
        BigDecimal interestRate = BigDecimal.valueOf(contractLeasePrice.getDefaultInterestRate()).divide(BigDecimal.valueOf(10000 * 100), 20, RoundingMode.HALF_UP);
        BigDecimal total = BigDecimal.ZERO;
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            if (Objects.equals(collectionBaseInfo.getWriteOffStatus(), CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())) {
                // 核销完毕的不用处理
                continue;
            }
            if (collectionBaseInfo.getPlanCollectionDate().isAfter(LocalDate.now())) {
                // 当前日期后面的租金不处理
                continue;
            }
            // 违约金= Σ((各期到期未付租金金额 * (提前还款日 - 应还日） * 合同罚息日利率) + (逾期还的租金金额 * (逾期还款日 - 应还日) * 合同罚息日利率) - (已核销的罚息))
            // 未还部分罚息
            long planRent = Optional.ofNullable(collectionBaseInfo.getPlanCollectionAmount()).orElse(0L);
            long actualRent = Optional.ofNullable(collectionBaseInfo.getCollectionAmount()).orElse(0L);
            BigDecimal penalty1 = BigDecimal.ZERO;
            if (actualRent < planRent) {
                long remainingRent = planRent - actualRent;
                long days = LocalDateTimeUtil.between(collectionBaseInfo.getPlanCollectionDate().atStartOfDay(), earlyRepayDate.atStartOfDay(), ChronoUnit.DAYS);
                penalty1 = penalty1.add(BigDecimal.valueOf(remainingRent).multiply(BigDecimal.valueOf(days)).multiply(interestRate));
            }
            // 逾期还款部分罚息
            BigDecimal penalty2 = BigDecimal.ZERO;
            List<CollectionRecordInfo> list = collectionRecordInfoMap.get(collectionBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(list)) {
                for (CollectionRecordInfo collectionRecordInfo : list) {
                    if (collectionBaseInfo.getPlanCollectionDate().isBefore(collectionRecordInfo.getCollectionDate())) {
                        long overdueDays = LocalDateTimeUtil.between(collectionBaseInfo.getPlanCollectionDate().atStartOfDay(), collectionRecordInfo.getCollectionDate().atStartOfDay(), ChronoUnit.DAYS);
                        penalty2 = penalty2.add(BigDecimal.valueOf(collectionRecordInfo.getCollectionAmount()).multiply(BigDecimal.valueOf(overdueDays)).multiply(interestRate));
                    }
                }
            }
            log.info("{}计算逾期已还租金的罚息:{}, 计算未还租金的罚息:{}, 已核销的罚息:{}", collectionBaseInfo.getCode(), penalty2.toPlainString(), penalty1.toPlainString(), collectionBaseInfo.getCollectionPenaltyInterest());
            total = total.add(penalty1).add(penalty2).subtract(BigDecimal.valueOf(Optional.ofNullable(collectionBaseInfo.getCollectionPenaltyInterest()).orElse(0L)));
        }
        return Util.mithrasLongDecimalTwo(total.longValue());
    }

    private void check(ContractPrepayment contractPrepayment) {
        if (Objects.equals(contractPrepayment.getIsEarlySettle(), YesOrNoNumberEnum.YES.getCode())) {
            // 提前结清的话需要检验保证金相关字段
            Assert.notNull(contractPrepayment.getIsEarnestMoneyDeduction(), () -> MithrasException.newException("<保证金是否抵扣>不能为空"));
            if (Objects.equals(contractPrepayment.getIsEarnestMoneyDeduction(), YesOrNoNumberEnum.YES.getCode())) {
                Assert.notNull(contractPrepayment.getEarnestMoneyDeductionAmount(), () -> MithrasException.newException("<保证金抵扣金额>不能为空"));
            }
            Assert.notBlank(contractPrepayment.getRemark(), () -> MithrasException.newException("<提前结清说明>不能为空"));
        }
    }
}
